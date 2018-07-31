package org.stepik.core.actions.step

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.project.Project
import org.stepik.core.actions.getShortcutText
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.metrics.Metrics
import org.stepik.core.pluginId

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
    
    abstract fun getLink(project: Project, stepNode: StudyNode): String
    
    override fun update(e: AnActionEvent?) {
        super.update(e)
        val presentation = e?.presentation ?: return
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
        private val ACTION_ID = "$pluginId.OpenInBrowser"
        private const val SHORTCUT = "ctrl shift pressed HOME"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "View this step on Hyperskill ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "View this step on Hyperskill"
    }
}
