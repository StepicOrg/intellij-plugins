package org.stepik.plugin.java.project.wizard;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbModePermission;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.InvalidDataException;
import com.jetbrains.tmp.learning.StudyProjectComponent;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.stepik.from.edu.intellij.utils.generation.*;
import org.stepik.from.edu.intellij.utils.generation.builders.CourseBuilder;
import org.stepik.from.edu.intellij.utils.generation.builders.LessonBuilder;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;

public class JavaCourseBuilder extends JavaModuleBuilder implements CourseBuilder {
    private static final Logger logger = Logger.getInstance(JavaCourseBuilder.class);
    private StepikProjectGenerator generator;

    @Override
    public void createCourseFromGenerator(
            @NotNull ModifiableModuleModel moduleModel,
            Project project,
            EduProjectGenerator generator)
            throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        generator.generateProject(project, project.getBaseDir());

        Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null) {
            logger.info("failed to generate builders");
            return;
        }
        course.setCourseMode(EduNames.STEPIK_CODE);

        String moduleDir = getModuleFileDirectory();
        if (moduleDir == null) {
            return;
        }

        logger.info("Module dir = " + moduleDir);
        EduUtilModuleBuilder utilModuleBuilder = new EduUtilModuleBuilder(moduleDir);
        Module utilModule = utilModuleBuilder.createModule(moduleModel);

        createLessonModules(moduleModel, course, moduleDir, utilModule);

        ApplicationManager.getApplication().invokeLater(
                () -> DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND,
                        () -> ApplicationManager.getApplication().runWriteAction(
                                () -> StudyProjectComponent.getInstance(project)
                                        .registerStudyToolWindow(course))));
    }

    @Override
    public void createLessonModules(
            @NotNull ModifiableModuleModel moduleModel, Course course,
            String moduleDir, Module utilModule) throws InvalidDataException,
            IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        int sectionIndex = 0;
        int lessonIndex = 1;
        for (Section section : course.getSections()) {
            section.setIndex(++sectionIndex);
            LessonBuilder sectionBuilder = new StepikJavaSectionBuilder(moduleDir, section, utilModule);
            sectionBuilder.createLesson(moduleModel);
            for (Lesson lesson : section.getLessons()) {
                lesson.setIndex(lessonIndex++);
                String sectionDir = moduleDir + "/" + section.getDirectory();
                LessonBuilder lessonBuilder = new StepikJavaLessonBuilder(sectionDir, lesson, utilModule);
                lessonBuilder.createLesson(moduleModel);
            }
        }
    }

    @Override
    public ModuleType getModuleType() {
        return StepikModuleType.STEPIK_MODULE_TYPE;
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return ProjectWizardStepFactory.getInstance()
                .createJavaSettingsStep(settingsStep, this, Conditions.alwaysTrue());
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel)
            throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module baseModule = super.createModule(moduleModel);
        Project project = baseModule.getProject();
        logger.info("create module - login");
        StepikConnectorLogin.loginFromDialog(project);
        createCourseFromGenerator(moduleModel, project, getGenerator());
        return baseModule;
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(
            @NotNull WizardContext wizardContext,
            @NotNull ModulesProvider modulesProvider) {
        ModuleWizardStep[] previousWizardSteps = super.createWizardSteps(wizardContext, modulesProvider);
        ModuleWizardStep[] wizardSteps = new ModuleWizardStep[previousWizardSteps.length + 1];

        wizardSteps[0] = new SelectCourseWizardStep(getGenerator(), wizardContext);

        return wizardSteps;
    }

    private StepikProjectGenerator getGenerator() {
        if (generator == null) {
            generator = new StepikProjectGenerator();
        }
        return generator;
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        setSourcePaths(Collections.emptyList());
        super.setupRootModel(rootModel);
    }
}