package org.stepik.core.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.stepik.core.StudyUtils.pluginId
import org.stepik.core.auth.StepikAuthManager.authentication
import org.stepik.core.auth.StepikAuthManager.isAuthenticated

class LoginAction : StudyActionWithShortcut(TEXT, DESCRIPTION) {
    override fun actionPerformed(e: AnActionEvent?) {
        authentication()
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun update(e: AnActionEvent?) {
        e?.presentation?.isVisible = !isAuthenticated
    }

    companion object {
        private val ACTION_ID = "$pluginId.LoginAction"
        private const val SHORTCUT = "ctrl alt pressed L"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Login on Stepik ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Login on Stepik"
    }
}
