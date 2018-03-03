package org.stepik.core.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import org.stepik.core.StudyUtils.isStepikProject
import org.stepik.core.common.Loggable
import org.stepik.core.icons.AllStepikIcons

class StudyToolWindowFactory : ToolWindowFactory, DumbAware, Loggable {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.icon = AllStepikIcons.stepikLogoSmall

        if (!isStepikProject(project)) {
            logger.warn("Study Tool Window did not create")
            return
        }

        val studyToolWindow = StudyToolWindow()
        studyToolWindow.init(project)
        val contentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(studyToolWindow, null, false)
        contentManager.addContent(content)
        Disposer.register(project, studyToolWindow)
        logger.info("Study Tool Window is created")
    }

    companion object {
        const val STUDY_TOOL_WINDOW = "Step Description"
    }
}
