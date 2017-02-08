package org.stepik.plugin.projectWizard.idea;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.courses.Course;
import org.stepik.plugin.projectWizard.StepikProjectGenerator;
import org.stepik.plugin.projectWizard.ui.ProjectSettingsPanel;

import javax.swing.*;

class JavaWizardStep extends ModuleWizardStep {
    private static final Logger logger = Logger.getInstance(JavaWizardStep.class);
    private final StepikProjectGenerator generator;
    private final ProjectSettingsPanel panel;
    private final Project project;
    private boolean valid;
    private boolean leaving;

    JavaWizardStep(@NotNull final StepikProjectGenerator generator, @NotNull Project project) {
        this.generator = generator;
        this.project = project;
        panel = new ProjectSettingsPanel(project, true);
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return panel.getComponent();
    }

    @Override
    public void updateDataModel() {
    }

    @Override
    public void updateStep() {
        panel.updateStep();
        valid = false;
        leaving = false;
    }

    @Override
    public boolean validate() throws ConfigurationException {
        valid = panel.validate();
        return valid;
    }

    @Override
    public void onStepLeaving() {
        leaving = true;
    }

    @Override
    public void onWizardFinished() throws CommitStepException {
        if (!(valid && leaving)) {
            return;
        }

        SupportedLanguages selectedLang = panel.getLanguage();
        generator.setDefaultLang(selectedLang);
        Course selectedCourse = panel.getSelectedCourse();
        generator.createCourseNodeUnderProgress(project, selectedCourse);

        long id = selectedCourse.getId();

        if (id == 0) {
            return;
        }

        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
        try {
            stepikApiClient.enrollments()
                    .post()
                    .course(id)
                    .execute();
        } catch (StepikClientException e) {
            String message = String.format("Can't enrollment on a course: id = %s, name = %s",
                    id, selectedCourse.getTitle());
            logger.error(message, e);
        }
        logger.info(String.format("Leaving step the project wizard with the selected course: id = %s, name = %s",
                id, selectedCourse.getTitle()));
    }

    Course getSelectedCourse() {
        return panel.getSelectedCourse();
    }
}