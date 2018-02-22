package org.stepik.core

import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import org.stepik.api.exceptions.StepikClientException
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated
import org.stepik.core.ui.StudyToolWindow
import org.stepik.core.ui.StudyToolWindowFactory
import org.stepik.core.ui.StudyToolWindowFactory.Companion.STUDY_TOOL_WINDOW
import org.stepik.core.utils.ProjectFilesUtils
import java.util.regex.Pattern


object StudyUtils : Loggable {
    private const val PATH_PATTERN = "^(?:(?:section([0-9]+)/lesson([0-9]+)/step([0-9]+))|" +
            "(?:lesson([0-9]+)/step([0-9]+))|" +
            "(?:step([0-9]+))|" +
            "(?:section([0-9]+)/lesson([0-9]+))|" +
            "(?:section([0-9]+))" +
            ").*$"
    private val pathPattern by lazy {
        Pattern.compile(PATH_PATTERN)
    }

    fun initToolWindows(project: Project) {
        val toolWindow = ToolWindowManager.getInstance(project)
                .getToolWindow(STUDY_TOOL_WINDOW)
        toolWindow.contentManager.removeAllContents(false)
        StudyToolWindowFactory().createToolWindowContent(project, toolWindow)
    }

    fun getStudyToolWindow(project: Project): StudyToolWindow? {
        if (project.isDisposed) {
            return null
        }
        val toolWindowManager = ToolWindowManager.getInstance(project) ?: return null
        val contents = toolWindowManager.getToolWindow(STUDY_TOOL_WINDOW)
                ?.contentManager?.contents
        return contents?.mapNotNull { it.component as? StudyToolWindow }?.firstOrNull()
    }

    fun getConfigurator(project: Project): StudyPluginConfigurator? {
        return getProjectManager(project)?.getConfigurator(project)
    }

    fun getProjectManager(project: Project): ProjectManager? {
        return getService(project, ProjectManager::class.java)
    }

    private fun getRelativePath(project: Project, item: VirtualFile): String {
        val basePath = project.basePath ?: return item.path

        return ProjectFilesUtils.getRelativePath(basePath, item.path)
    }

    fun getStudyNode(project: Project, nodeVF: VirtualFile): StudyNode? {
        val root = getProjectManager(project)?.projectRoot ?: return null
        val path = getRelativePath(project, nodeVF)

        return getStudyNode(root, path)
    }

    fun getStudyNode(root: StudyNode, relativePath: String): StudyNode? {
        var myRoot: StudyNode? = root
        if (relativePath == ".") {
            return myRoot
        }

        val matcher = pathPattern.matcher(relativePath)
        if (!matcher.matches()) {
            return null
        }

        (1..matcher.groupCount()).mapNotNull { matcher.group(it) }
                .forEach { myRoot = myRoot?.getChildById(it.toLong()) ?: return null }

        return myRoot
    }

    fun getRecommendation(root: StudyNode): StudyNode? {
        val data = root.data
        if (!data.isAdaptive) {
            return null
        }

        var studyNode: StudyNode? = null

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
                val lesson = recommendations.first.lesson

                val steps = stepikClient.steps()
                        .get()
                        .lesson(lesson)
                        .execute()
                if (!steps.isEmpty) {
                    val stepId = steps.first.id
                    studyNode = root.getChildByClassAndId(StepNode::class.java, stepId)
                }
            }
        } catch (e: StepikClientException) {
            logger.warn(e)
        }

        return studyNode
    }

    fun isStepikProject(project: Project?): Boolean {
        project ?: return false
        return getProjectManager(project)?.projectRoot != null
    }
}
