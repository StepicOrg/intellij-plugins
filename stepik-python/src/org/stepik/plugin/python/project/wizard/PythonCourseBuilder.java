package org.stepik.plugin.python.project.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbModePermission;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.InvalidDataException;
import com.jetbrains.edu.learning.StudyProjectComponent;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.edu.learning.core.EduNames;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.courseFormat.Lesson;
import com.jetbrains.edu.learning.stepik.StepikConnectorLogin;
import com.jetbrains.edu.utils.generation.*;
import com.jetbrains.edu.utils.generation.builders.CourseBuilder;
import com.jetbrains.edu.utils.generation.builders.LessonBuilder;
import com.jetbrains.python.module.PythonModuleBuilder;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class PythonCourseBuilder extends PythonModuleBuilder implements CourseBuilder {
    private static final Logger LOG = Logger.getInstance(PythonCourseBuilder.class);
    private StepikProjectGenerator generator;

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module baseModule = super.createModule(moduleModel);
        Project project = baseModule.getProject();
        LOG.warn("login dialog");
        StepikConnectorLogin.loginFromDialog(project);

        createCourseFromGenerator(moduleModel, project, getGenerator());
        return baseModule;
    }

    public void createCourseFromGenerator(@NotNull ModifiableModuleModel moduleModel, Project project, EduProjectGenerator generator) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        generator.generateProject(project, project.getBaseDir());

        Course course = StudyTaskManager.getInstance(project).getCourse();
        course.setCourseMode(EduNames.STEPIK_CODE);
//        builders.setCourseMode(EduNames.STUDY);
        if (course == null) {
            LOG.info("failed to generate builders");
            return;
        }
        String moduleDir = getModuleFileDirectory();
        if (moduleDir == null) {
            return;
        }

        EduUtilModuleBuilder utilModuleBuilder = new EduUtilModuleBuilder(moduleDir);
        Module utilModule = utilModuleBuilder.createModule(moduleModel);

        createLessonModules(moduleModel, course, moduleDir, utilModule);

        ApplicationManager.getApplication().invokeLater(
                () -> DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND,
                        () -> ApplicationManager.getApplication().runWriteAction(() -> {
                            StudyProjectComponent.getInstance(project).registerStudyToolWindow(course);
                        })));
    }

    @Override
    public void createLessonModules(@NotNull ModifiableModuleModel moduleModel, Course course, String moduleDir, Module utilModule) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        {
            List<Lesson> lessons = course.getLessons();
            for (int i = 0; i < lessons.size(); i++) {
                int lessonVisibleIndex = i + 1;
                Lesson lesson = lessons.get(i);
                lesson.setIndex(lessonVisibleIndex);

                StepikSectionDirBuilder dirBuilder = new StepikSectionDirBuilder(moduleDir, lesson);
                dirBuilder.build();
//
                LessonBuilder lessonBuilder =  new StepikPythonLessonBuilder(dirBuilder.getSectionDir() , lesson, utilModule);
//            StepikLessonModuleBuilder stepikLessonModuleBuilder =  new StepikLessonModuleBuilder(moduleDir, lesson, utilModule);
                lessonBuilder.createLesson(moduleModel);
            }
        }
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        ModuleWizardStep[] previousWizardSteps = super.createWizardSteps(wizardContext, modulesProvider);
        ModuleWizardStep[] wizardSteps = new ModuleWizardStep[previousWizardSteps.length+1];

        wizardSteps[0] = new SelectCourseWizardStep(getGenerator(), wizardContext);
//        wizardSteps[0] = new StepikProjectPanel(this, wizardContext);
        for (int i = 0; i < previousWizardSteps.length; i++) {
            wizardSteps[i+1] = previousWizardSteps[i];
        }

        return wizardSteps;
    }

    private StepikProjectGenerator getGenerator(){
        if (generator == null){
            generator = new StepikProjectGenerator();
        }
        return generator;
    }
}