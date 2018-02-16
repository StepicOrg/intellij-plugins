package org.stepik.core.projectWizard.pycharm

import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.application.ApplicationManager
import org.stepik.core.SupportedLanguages
import org.stepik.core.projectWizard.ui.ProjectSettingListener
import org.stepik.core.projectWizard.ui.ProjectSettingsPanel
import org.stepik.core.stepik.StepikAuthManager
import org.stepik.core.stepik.StepikAuthManager.authentication
import org.stepik.core.stepik.StepikAuthManagerListener
import org.stepik.core.stepik.StepikAuthState
import org.stepik.core.stepik.StepikAuthState.AUTH
import org.stepik.core.stepik.StepikAuthState.NOT_AUTH

internal class PyCharmWizardStep(private val generator: StepikPyProjectGenerator) :
        ProjectSettingListener, StepikAuthManagerListener {
    private val invalidCourse = ValidationResult("Please, select a course")
    private val needLogin = ValidationResult("Please, you must login")
    private val panel: ProjectSettingsPanel = ProjectSettingsPanel(false)
    private var authenticated = NOT_AUTH

    val selectedStudyObject = panel.selectedStudyObject

    val component = panel.getComponent()

    init {
        panel.addListener(this)
        authenticated = authentication(false)
        StepikAuthManager.addListener(this)
    }

    fun check(): ValidationResult {
        val selectedStudyObject = panel.selectedStudyObject

        if (selectedStudyObject.id == 0L) {
            return invalidCourse
        }

        return if (authenticated != AUTH) {
            needLogin
        } else ValidationResult.OK
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
