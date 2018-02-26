package org.stepik.core.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.ServiceManager
import org.stepik.core.ProjectManager
import org.stepik.core.auth.StepikAuthManager
import org.stepik.core.auth.StepikAuthManager.authentication
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.auth.StepikAuthManager.logout
import org.stepik.core.auth.StepikAuthManagerListener
import org.stepik.core.auth.StepikAuthState
import org.stepik.core.auth.StepikAuthState.AUTH
import org.stepik.core.auth.StepikAuthState.NOT_AUTH
import org.stepik.core.utils.Utils
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

internal class StepikSettingsPanel : StepikAuthManagerListener {
    private var pane: JPanel? = null
    private var hintCheckBox: JCheckBox? = null
    private var logoutButton: JButton? = null
    private var userName: JLabel? = null

    var isModified: Boolean = false
        private set
    private var projectManager: ProjectManager? = null

    val panel: JComponent?
        get() = pane

    init {
        initProjectOfSettings()
        hintCheckBox!!.addActionListener { isModified = true }
        logoutButton!!.addActionListener {
            if (isAuthenticated) {
                logout()
            } else {
                authentication()
            }
        }

        StepikAuthManager.addListener(this)
    }

    private fun updateUserName() {
        userName!!.text = StepikAuthManager.currentUserFullName
    }

    private fun initProjectOfSettings() {
        projectManager = ServiceManager.getService(Utils.currentProject, ProjectManager::class.java)
        hintCheckBox!!.isSelected = projectManager != null && projectManager!!.showHint
        logoutButton!!.text = if (isAuthenticated) "Logout" else "Login"
    }

    fun reset() {
        initProjectOfSettings()
        resetModification()
        updateUserName()
    }

    fun apply() {
        if (isModified && projectManager != null) {
            projectManager!!.showHint = hintCheckBox!!.isSelected
        }
        resetModification()
    }

    private fun resetModification() {
        isModified = false
    }

    override fun stateChanged(oldState: StepikAuthState, newState: StepikAuthState) {
        if (newState === NOT_AUTH || newState === AUTH) {
            ApplicationManager.getApplication().invokeLater({
                updateUserName()
                logoutButton!!.text = if (newState === AUTH) "Logout" else "Login"
            }, ModalityState.stateForComponent(pane!!))
        }
    }

    fun dispose() {
        StepikAuthManager.removeListener(this)
    }
}
