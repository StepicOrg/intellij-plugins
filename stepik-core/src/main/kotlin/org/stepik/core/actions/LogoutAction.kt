package org.stepik.core.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.stepik.core.stepik.StepikAuthManager
import org.stepik.core.stepik.StepikAuthManager.isAuthenticated

class LogoutAction : StudyActionWithShortcut(TEXT, DESCRIPTION) {
    override fun actionPerformed(e: AnActionEvent?) {
        StepikAuthManager.logout()
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = emptyArray<String>()

    override fun update(e: AnActionEvent?) {
        e?.presentation?.isVisible = isAuthenticated
    }

    companion object {
        private const val ACTION_ID = "STEPIK.LogoutAction"
        private const val TEXT = "Logout from Stepik"
        private const val DESCRIPTION = "Logout on Stepik"
    }
}
