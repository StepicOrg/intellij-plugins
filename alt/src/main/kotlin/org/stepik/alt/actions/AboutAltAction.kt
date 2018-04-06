package org.stepik.alt.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.ex.MessagesEx
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.pluginId

class AboutAction : StudyActionWithShortcut(TEXT, DESCRIPTION) {
    override fun actionPerformed(e: AnActionEvent?) {
        MessagesEx.showInfoMessage("More info on https://alt.stepik.org", TEXT)
    }
    
    override fun getActionId() = ACTION_ID
    
    override fun getShortcuts() = emptyArray<String>()
    
    companion object {
        private val ACTION_ID = "$pluginId.AboutAction"
        private const val TEXT = "About Stepik ALT"
        private const val DESCRIPTION = "About Stepik ALT"
    }
}
