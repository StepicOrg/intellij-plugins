package org.stepik.plugin.projectWizard.pycharm

import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.application.ApplicationManager
import org.stepik.core.SupportedLanguages
import org.stepik.core.auth.StepikAuthManager
import org.stepik.core.auth.StepikAuthManager.authentication
import org.stepik.core.auth.StepikAuthManagerListener
import org.stepik.core.auth.StepikAuthState
import org.stepik.core.auth.StepikAuthState.AUTH
import org.stepik.plugin.projectWizard.ui.ProjectSettingListener
import org.stepik.plugin.projectWizard.ui.ProjectSettingsPanel

internal class PyCharmWizardStep(private val generator: StepikPyProjectGenerator) :
        ProjectSettingListener, StepikAuthManagerListener {
    private val invalidCourse = ValidationResult("Please, select a course")
    private val needLogin = ValidationResult("Please, you must login")
    private val panel: ProjectSettingsPanel = ProjectSettingsPanel(false)
    private var authenticated = authentication(false)

    val selectedStudyObject = panel.selectedStudyObject

    val component = panel.getComponent()

    init {
        panel.addListener(this)
        StepikAuthManager.addListener(this)
    }

    fun check(): ValidationResult {
        return when {
            panel.selectedStudyObject.id == 0L -> invalidCourse
            authenticated != AUTH -> needLogin
            else -> ValidationResult.OK
        }
    }

    override fun changed() {
        generator.fireStateChanged()
    }

    fun updateStep() {
        panel.language = SupportedLanguages.PYTHON3
        panel.updateStep()
    }

    fun dispose() {
        panel.dispose()
    }

    override fun stateChanged(oldState: StepikAuthState, newState: StepikAuthState) {
        authenticated = newState
        ApplicationManager.getApplication().invokeLater { generator.fireStateChanged() }
    }
}
