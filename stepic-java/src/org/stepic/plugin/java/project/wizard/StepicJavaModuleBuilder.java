package org.stepic.plugin.java.project.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.InvalidDataException;
import com.jetbrains.edu.learning.stepic.CourseInfo;
import com.jetbrains.edu.learning.stepic.StepicConnectorGet;
import com.jetbrains.edu.learning.stepic.StepicConnectorLogin;
import com.jetbrains.edu.utils.generation.EduProjectGenerator;
import com.jetbrains.edu.utils.generation.StepicCourseModuleBuilder;
import com.jetbrains.edu.utils.generation.StepicModuleWizardStep;
import com.jetbrains.edu.utils.generation.StepicProjectPanel;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepic.plugin.java.StepicJavaCourseConfigurator;

import javax.swing.*;
import java.io.IOException;

public class StepicJavaModuleBuilder extends StepicCourseModuleBuilder {
    private static final Logger LOG = Logger.getInstance(StepicJavaModuleBuilder.class);


    @Override
    public String getBuilderId() {
        return "java.stepic.builder";
    }

    @Nullable
    @Override
    public Module commitModule(@NotNull Project project, @Nullable ModifiableModuleModel model) {
        Module baseModule = super.commitModule(project, model);
        new StepicJavaCourseConfigurator().configureModule(project);
        return baseModule;
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, this, Conditions.alwaysTrue());
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module baseModule = super.createModule(moduleModel);
        Project project = baseModule.getProject();
        EduProjectGenerator generator = new EduProjectGenerator();
        StepicConnectorLogin.login(project);

        JPanel panel = new StepicProjectPanel(generator);
        panel.setVisible(true);

        CourseInfo courseInfo = StepicConnectorGet.getDefaultCourse();
        if (courseInfo == null) {
            LOG.info("Failed to find course ");
            return baseModule;
        } else {
            LOG.warn("succes!!!11");
        }

        createCourseFromCourseInfo(moduleModel, project, generator, courseInfo);
//        generator.generateProject(project, project.getBaseDir());
        return baseModule;
    }


    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        ModuleWizardStep[] previonsWizardSteps = super.createWizardSteps(wizardContext, modulesProvider);
        ModuleWizardStep[] wizardSteps = new ModuleWizardStep[previonsWizardSteps.length+1];

        wizardSteps[0] = new StepicModuleWizardStep(this, wizardContext);
//        wizardSteps[0] = new StudyNewProjectPanel(this, wizardContext);
        for (int i = 0; i < previonsWizardSteps.length; i++) {
            wizardSteps[i+1] = previonsWizardSteps[i];
        }

        return wizardSteps;
    }



}