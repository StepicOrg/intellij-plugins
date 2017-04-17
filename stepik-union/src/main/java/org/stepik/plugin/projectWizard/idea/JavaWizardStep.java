package org.stepik.plugin.projectWizard.idea;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.StudyObject;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.projectWizard.ProjectWizardUtils;
import org.stepik.core.stepik.StepikAuthManager;
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
        panel = new ProjectSettingsPanel(true);
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
        StepikAuthManager.authentication(true);
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
        StudyObject studyObject = panel.getSelectedStudyObject();
        generator.createCourseNodeUnderProgress(project, studyObject);

        long id = studyObject.getId();

        if (id == 0) {
            return;
        }

        ProjectWizardUtils.enrollmentCourse(studyObject);

        String messageTemplate = "Leaving step the project wizard with the selected study object: type=%s, id = %s, name = %s";
        logger.info(String.format(messageTemplate, studyObject.getClass().getSimpleName(), id, studyObject.getTitle()));
    }

    StudyObject getSelectedStudyObject() {
        return panel.getSelectedStudyObject();
    }
}