package org.stepic.plugin.java.project.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.InvalidDataException;
import com.jetbrains.edu.learning.stepic.CourseInfo;
import com.jetbrains.edu.learning.stepic.EduStepicConnector;
import com.jetbrains.edu.utils.generation.EduCourseModuleBuilder;
import com.jetbrains.edu.utils.generation.EduProjectGenerator;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepic.plugin.java.StepicJavaCourseConfigurator;

import java.io.IOException;

public class StepicJavaModuleBuilder extends EduCourseModuleBuilder {
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
//        StudyProjectGenerator generator = new StudyProjectGenerator();
//        File courseRoot = EduIntellijUtils.getBundledCourseRoot(DEFAULT_COURSE_NAME, EduKotlinKoansModuleBuilder.class);
//        CourseInfo courseInfo = generator.addLocalCourse(FileUtil.join(courseRoot.getPath(), DEFAULT_COURSE_NAME));
//        CourseInfo courseInfo = EduStepicConnector.getCourses().get(0);
//        EduStepicConnector.showLoginDialog();
        EduStepicConnector.login("step@kismail.ru", "123456");
        CourseInfo courseInfo = EduStepicConnector.getDefaultCourse();
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


}