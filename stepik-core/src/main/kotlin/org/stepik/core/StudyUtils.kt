package org.stepik.core

import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.steps.Step
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated
import org.stepik.core.ui.StudyToolWindow
import org.stepik.core.ui.StudyToolWindowFactory
import org.stepik.core.utils.ProjectFilesUtils
import java.util.regex.Pattern

object StudyUtils : Loggable {
    private const val PATH_PATTERN = "^(?:(?:section([0-9]+)/lesson([0-9]+)/step([0-9]+))|" +
            "(?:lesson([0-9]+)/step([0-9]+))|" +
            "(?:step([0-9]+))|" +
            "(?:section([0-9]+)/lesson([0-9]+))|" +
            "(?:section([0-9]+))" +
            ").*$"
    private var pathPattern: Pattern? = null

    fun initToolWindows(project: Project) {
        val windowManager = ToolWindowManager.getInstance(project)
        windowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW)
                .contentManager
                .removeAllContents(false)
        val factory = StudyToolWindowFactory()
        factory.createToolWindowContent(project, windowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW))
    }

    fun getStudyToolWindow(project: Project): StudyToolWindow? {
        if (project.isDisposed) {
            return null
        }
        val toolWindowManager = ToolWindowManager.getInstance(project) ?: return null
        val toolWindow = toolWindowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW)
        if (toolWindow != null) {
            val contents = toolWindow.contentManager.contents
            for (content in contents) {
                val component = content.component
                if (component != null && component is StudyToolWindow) {
                    return component
                }
            }
        }
        return null
    }

    fun getConfigurator(project: Project): StudyPluginConfigurator? {
        val extensions = StudyPluginConfigurator.EP_NAME.extensions
        return extensions.firstOrNull { it.accept(project) }
    }

    private fun getRelativePath(project: Project, item: VirtualFile): String {
        val path = item.path
        val basePath = project.basePath ?: return path

        return ProjectFilesUtils.getRelativePath(basePath, path)
    }

    fun getStudyNode(project: Project, nodeVF: VirtualFile): StudyNode<*, *>? {
        val path = getRelativePath(project, nodeVF)
        val projectManager = getService(project, ProjectManager::class.java)
        val root = projectManager.projectRoot ?: return null

        return getStudyNode(root, path)
    }

    fun getStudyNode(root: StudyNode<*, *>, relativePath: String): StudyNode<*, *>? {
        var myRoot: StudyNode<*, *>? = root
        if (relativePath == ".") {
            return myRoot
        }

        if (pathPattern == null) {
            pathPattern = Pattern.compile(PATH_PATTERN)
        }
        val matcher = pathPattern!!.matcher(relativePath)
        if (!matcher.matches()) {
            return null
        }

        for (i in 1..matcher.groupCount()) {
            val idString = matcher.group(i) ?: continue

            val id = Integer.parseInt(idString)

            myRoot = myRoot?.getChildById(id.toLong())
            if (myRoot == null) {
                return null
            }
        }

        return myRoot
    }

    fun getRecommendation(root: StudyNode<*, *>): StudyNode<*, *>? {
        val data = root.data
        if (data == null || !data.isAdaptive) {
            return null
        }

        var studyNode: StudyNode<*, *>? = null

        val stepikClient = authAndGetStepikApiClient()
        if (!isAuthenticated) {
            return null
        }

        try {
            val recommendations = stepikClient.recommendations()
                    .get()
                    .course(root.id)
                    .execute()
            if (!recommendations.isEmpty) {
                val recommendation = recommendations.first

                val lesson = recommendation.lesson

                val steps = stepikClient.steps()
                        .get()
                        .lesson(lesson)
                        .execute()
                if (!steps.isEmpty) {
                    val stepId = steps.first.id
                    studyNode = root.getChildByClassAndId(Step::class.java, stepId)
                }
            }
        } catch (e: StepikClientException) {
            logger.warn(e)
        }

        return studyNode
    }

    fun isStepikProject(project: Project?): Boolean {
        if (project == null) {
            return false
        }
        return getService(project, ProjectManager::class.java) != null
    }
}
