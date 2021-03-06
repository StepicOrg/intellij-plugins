package org.stepik.core

import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import org.stepik.api.exceptions.StepikClientException
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.ui.StudyToolWindow
import org.stepik.core.ui.StudyToolWindowFactory
import org.stepik.core.ui.StudyToolWindowFactory.Companion.STUDY_TOOL_WINDOW
import org.stepik.core.utils.getRelativePath

private val logging = object : Loggable {}

private val pathPattern by lazy {
    ("^(?:(?:section([0-9]+)/lesson([0-9]+)/step([0-9]+))|" +
     "(?:lesson([0-9]+)/step([0-9]+))|" +
     "(?:step([0-9]+))|" +
     "(?:section([0-9]+)/lesson([0-9]+))|" +
     "(?:section([0-9]+))" +
     ").*$"
    ).toRegex()
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
    return contents?.mapNotNull { it.component as? StudyToolWindow }
            ?.firstOrNull()
}

fun getConfigurator(project: Project): StudyPluginConfigurator? {
    return getProjectManager(project)?.getConfigurator(project)
}

private fun getRelativePath(project: Project, item: VirtualFile): String {
    val basePath = project.basePath ?: return item.path
    return basePath.getRelativePath(item.path)
}

fun getStudyNode(project: Project, nodeVF: VirtualFile): StudyNode? {
    val root = getProjectRoot(project) ?: return null
    val path = getRelativePath(project, nodeVF)
    
    return getStudyNode(root, path)
}

fun getStudyNode(root: StudyNode, relativePath: String): StudyNode? {
    if (relativePath == ".") {
        return root
    }
    
    val matcher = pathPattern.matchEntire(relativePath) ?: return null
    
    return matcher.groupValues.drop(1)
            .filterNot { it.isEmpty() }
            .map { it.toLong() }
            .fold(root as StudyNode?) { newRoot, id ->
                newRoot?.getChildById(id)
            }
}

fun getProjectManager(project: Project?): ProjectManager? {
    project ?: return null
    return getService(project, ProjectManager::class.java)
}

fun getProjectRoot(project: Project?): StudyNode? {
    return getProjectManager(project)?.projectRoot
}

fun isStepikProject(project: Project?): Boolean {
    return getProjectRoot(project) != null
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
        if (recommendations.isNotEmpty) {
            val lesson = recommendations.first()
                    .lesson
            
            val step = stepikClient.steps()
                    .get()
                    .lesson(lesson)
                    .execute()
                    .firstOrNull()
            if (step != null) {
                val stepId = step.id
                studyNode = root.getChildByClassAndId(StepNode::class.java, stepId)
            }
        }
    } catch (e: StepikClientException) {
        logging.logger.warn(e)
    }
    
    return studyNode
}

val pluginId = getService(PluginSettings::class.java).pluginId

val pluginMetricsName = getService(PluginSettings::class.java).pluginMetricsName

val host = getService(PluginSettings::class.java).host

val clientId = getService(PluginSettings::class.java).clientId

val loadCurrentUser = getService(PluginSettings::class.java)::currentUser

val pluginName = getService(PluginSettings::class.java).pluginName
