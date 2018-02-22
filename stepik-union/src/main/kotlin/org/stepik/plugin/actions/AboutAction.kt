package org.stepik.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.ex.MessagesEx
import org.stepik.core.actions.StudyActionWithShortcut

class AboutAction : StudyActionWithShortcut(TEXT, DESCRIPTION) {
    override fun actionPerformed(e: AnActionEvent?) {
        MessagesEx.showInfoMessage("More info on https://stepik.org", TEXT)
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = emptyArray<String>()

    companion object {
        private const val ACTION_ID = "STEPIK.AboutAction"
        private const val TEXT = "About Stepik Plugin"
        private const val DESCRIPTION = "About Stepik Plugin"
    }
}
