package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.StudyObject;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.stepik.StepikAuthManager;
import org.stepik.core.stepik.StepikAuthManagerListener;
import org.stepik.core.stepik.StepikAuthState;
import org.stepik.plugin.projectWizard.ui.ProjectSettingListener;
import org.stepik.plugin.projectWizard.ui.ProjectSettingsPanel;

import javax.swing.*;

import static org.stepik.core.stepik.StepikAuthManager.authentication;
import static org.stepik.core.stepik.StepikAuthState.AUTH;
import static org.stepik.core.stepik.StepikAuthState.NOT_AUTH;

class PyCharmWizardStep implements ProjectSettingListener, StepikAuthManagerListener {
    private final ValidationResult invalidCourse = new ValidationResult("Please, select a course");
    private final ValidationResult needLogin = new ValidationResult("Please, you must login");
    private final StepikPyProjectGenerator generator;
    private final ProjectSettingsPanel panel;
    private StepikAuthState authenticated = NOT_AUTH;

    PyCharmWizardStep(@NotNull StepikPyProjectGenerator generator, @NotNull Project project) {
        this.generator = generator;
        panel = new ProjectSettingsPanel(false);
        panel.addListener(this);
        authenticated = authentication(false);
        StepikAuthManager.addListener(this);
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

        if (authenticated != AUTH) {
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

    @Override
    public void stateChanged(@NotNull StepikAuthState oldState, @NotNull StepikAuthState newState) {
        authenticated = newState;
        ApplicationManager.getApplication().invokeLater(generator::fireStateChanged);
    }
}