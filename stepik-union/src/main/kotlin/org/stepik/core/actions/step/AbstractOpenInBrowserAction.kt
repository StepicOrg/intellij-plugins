package org.stepik.core.actions.step

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.project.Project
import icons.AllStepikIcons
import org.stepik.core.actions.getShortcutText
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.metrics.Metrics

abstract class AbstractOpenInBrowserAction : AbstractStepAction(TEXT, DESCRIPTION, AllStepikIcons.stepikLogo) {

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        getApplication().executeOnPooledThread {
            val stepNode = getCurrentStep(project) ?: return@executeOnPooledThread
            val link = getLink(project, stepNode)
            BrowserUtil.browse(link)
            Metrics.openInBrowserAction(project, stepNode)
        }
    }

    abstract fun getLink(project: Project, stepNode: StepNode): String

    override fun update(e: AnActionEvent) {
        super.update(e)
        val presentation = e.presentation
        val project = e.project
        val stepNode = getCurrentStep(project)
        if (stepNode == null) {
            presentation.description = DESCRIPTION
            return
        }
        val link = getLink(project!!, stepNode)
        presentation.description = link
    }

    companion object {
        private const val ACTION_ID = "STEPIK.OpenInBrowser"
        private const val SHORTCUT = "ctrl shift pressed HOME"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "View this step on Stepik ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "View this step on Stepik"
    }
}
