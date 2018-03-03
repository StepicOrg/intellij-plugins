package org.stepik.plugin.projectWizard.idea

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import org.stepik.api.objects.StudyObject
import org.stepik.core.auth.StepikAuthManager.authentication
import org.stepik.core.auth.StepikAuthState.AUTH
import org.stepik.core.common.Loggable
import org.stepik.core.projectWizard.ProjectWizardUtils.enrollmentCourse
import org.stepik.plugin.projectWizard.StepikProjectGenerator
import org.stepik.plugin.projectWizard.ui.ProjectSettingsPanel

internal class JavaWizardStep(private val generator: StepikProjectGenerator,
                              private val project: Project) : ModuleWizardStep(), Loggable {
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

    override fun validate(): Boolean {
        if (authentication() != AUTH) {
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

    override fun onWizardFinished() {
        if (!(valid && leaving)) {
            return
        }

        generator.defaultLang = panel.language
        val studyObject = panel.selectedStudyObject
        generator.createCourseNodeUnderProgress(project, studyObject)

        val id = studyObject.id

        if (id == 0L) {
            return
        }

        if (!enrollmentCourse(studyObject)) {
            logger.warn("User didn't enrollment on course: $id")
        }

        logger.info("Leaving step the project wizard " +
                "with the selected study object: type=${studyObject.javaClass.simpleName}, " +
                "id = $id, name = ${studyObject.title}")
    }
}
