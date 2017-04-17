package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.StudyObject;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.stepik.StepikAuthManager;
import org.stepik.plugin.projectWizard.ui.ProjectSettingListener;
import org.stepik.plugin.projectWizard.ui.ProjectSettingsPanel;

import javax.swing.*;

import static org.stepik.core.stepik.StepikAuthManager.isAuthenticated;

class PyCharmWizardStep implements ProjectSettingListener {
    private final ValidationResult invalidCourse = new ValidationResult("Please, select a course");
    private final ValidationResult needLogin = new ValidationResult("Please, you must login");
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

        if (selectedStudyObject.getId() == 0) {
            return invalidCourse;
        }

        StepikAuthManager.authentication(false);
        if (!isAuthenticated()) {
            return needLogin;
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

    void dispose() {
        panel.dispose();
    }
}