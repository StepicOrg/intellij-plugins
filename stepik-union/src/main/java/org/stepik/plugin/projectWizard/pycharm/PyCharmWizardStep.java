package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.project.Project;
import org.stepik.core.SupportedLanguages;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.StudyObject;
import org.stepik.plugin.projectWizard.ui.ProjectSettingListener;
import org.stepik.plugin.projectWizard.ui.ProjectSettingsPanel;

import javax.swing.*;

class PyCharmWizardStep implements ProjectSettingListener {
    private final ValidationResult invalidCourse = new ValidationResult("Please select a course");
    private final ValidationResult adaptiveCourse = new ValidationResult(
            "Sorry, but we don't support adaptive courses yet");
    private final StepikPyProjectGenerator generator;
    private final ProjectSettingsPanel panel;

    PyCharmWizardStep(@NotNull StepikPyProjectGenerator generator, @NotNull Project project) {
        this.generator = generator;
        panel = new ProjectSettingsPanel(false);
        panel.addListener(this);
    }

    @NotNull
    StudyObject getSelectedStudyObject() {
        return panel.getSelectedStudyObject();
    }

    @NotNull
    JPanel getComponent() {
        return panel.getComponent();
    }

    @NotNull
    ValidationResult check() {
        StudyObject selectedStudyObject = panel.getSelectedStudyObject();

        if (selectedStudyObject.isAdaptive()) {
            return adaptiveCourse;
        }
        if (selectedStudyObject.getId() == 0) {
            return invalidCourse;
        }
        return ValidationResult.OK;
    }

    @Override
    public void changed() {
        generator.fireStateChanged();
    }

    void updateStep() {
        panel.setLanguage(SupportedLanguages.PYTHON3);
        panel.updateStep();
    }
}