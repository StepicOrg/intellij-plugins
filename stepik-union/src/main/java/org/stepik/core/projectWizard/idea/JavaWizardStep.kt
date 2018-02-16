package org.stepik.core.projectWizard.idea

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.wizard.CommitStepException
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import org.stepik.api.objects.StudyObject
import org.stepik.core.projectWizard.ProjectWizardUtils
import org.stepik.core.projectWizard.StepikProjectGenerator
import org.stepik.core.projectWizard.ui.ProjectSettingsPanel
import org.stepik.core.stepik.StepikAuthManager.authentication
import org.stepik.core.stepik.StepikAuthState.AUTH

internal class JavaWizardStep(private val generator: StepikProjectGenerator, private val project: Project) : ModuleWizardStep() {
    private val panel: ProjectSettingsPanel = ProjectSettingsPanel(true)
    private var valid: Boolean = false
    private var leaving: Boolean = false

    val selectedStudyObject: StudyObject
        get() = panel.selectedStudyObject

    override fun getComponent() = panel.getComponent()

    override fun updateDataModel() {}

    override fun updateStep() {
        panel.updateStep()
        valid = false
        leaving = false
    }

    @Throws(ConfigurationException::class)
    override fun validate(): Boolean {
        val authenticated = authentication(true)
        if (authenticated != AUTH) {
            throw ConfigurationException("Please, you should login", "Error")
        }
        valid = panel.validate()
        return valid
    }

    override fun onStepLeaving() {
        leaving = true
    }

    override fun disposeUIResources() {
        panel.dispose()
    }

    @Throws(CommitStepException::class)
    override fun onWizardFinished() {
        if (!(valid && leaving)) {
            return
        }

        val selectedLang = panel.language
        generator.defaultLang = selectedLang
        val studyObject = panel.selectedStudyObject
        generator.createCourseNodeUnderProgress(project, studyObject)

        val id = studyObject.id

        if (id == 0L) {
            return
        }

        val wasEnrollment = ProjectWizardUtils.enrollmentCourse(studyObject)
        if (wasEnrollment) {
            logger.warn("User didn't enrollment on course: " + id)
        }

        val messageTemplate = "Leaving step the project wizard with the selected study object: type=%s, id = %s, name = %s"
        logger.info(String.format(messageTemplate, studyObject.javaClass.simpleName, id, studyObject.title))
    }

    companion object {
        private val logger = Logger.getInstance(JavaWizardStep::class.java)
    }
}
