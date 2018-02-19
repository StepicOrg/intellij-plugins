package org.stepik.alt.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.ex.MessagesEx.showInfoMessage
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.actions.getShortcutText
import org.stepik.core.icons.AllStepikIcons

class StartAltAction : StudyActionWithShortcut(TEXT, DESCRIPTION, AllStepikIcons.stepikLogo) {
    override fun actionPerformed(e: AnActionEvent?) {
        showInfoMessage("ALT Started", "Started ALT")
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    companion object {
        private const val ACTION_ID = "Alt.StartAltAction"
        private const val SHORTCUT = "ctrl pressed ENTER"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Start Alt ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Start Learn with Stepik ALT"
    }
}
