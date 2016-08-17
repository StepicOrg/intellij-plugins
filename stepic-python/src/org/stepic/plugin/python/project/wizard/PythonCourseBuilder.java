package org.stepic.plugin.python.project.wizard;

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
import com.jetbrains.edu.learning.stepic.CourseInfo;
import com.jetbrains.edu.utils.generation.EduProjectGenerator;
import com.jetbrains.edu.utils.generation.EduUtilModuleBuilder;
import com.jetbrains.edu.utils.generation.StepicSectionDirBuilder;
import com.jetbrains.edu.utils.generation.builders.LessonBuilder;
import com.jetbrains.edu.utils.generation.builders.CourseBuilder;
import com.jetbrains.edu.utils.generation.StepicModuleWizardStep;
import com.jetbrains.python.module.PythonModuleBuilder;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class PythonCourseBuilder extends PythonModuleBuilder implements CourseBuilder {
    private static final Logger LOG = Logger.getInstance(PythonCourseBuilder.class);
    private EduProjectGenerator generator;

    @Override
    public void createCourseFromCourseInfo(@NotNull ModifiableModuleModel moduleModel, Project project, EduProjectGenerator generator, CourseInfo courseInfo) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        generator.setSelectedCourse(courseInfo);
        generator.generateProject(project, project.getBaseDir());

        Course course = StudyTaskManager.getInstance(project).getCourse();
        course.setCourseMode(EduNames.STEPIC_CODE);
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

                StepicSectionDirBuilder dirBuilder = new StepicSectionDirBuilder(moduleDir, lesson);
                dirBuilder.build();
//
                LessonBuilder lessonBuilder =  new StepicPythonLessonBuilder(dirBuilder.getSectionDir() , lesson, utilModule);
//            StepicLessonModuleBuilder stepicLessonModuleBuilder =  new StepicLessonModuleBuilder(moduleDir, lesson, utilModule);
                lessonBuilder.createLesson(moduleModel);
            }
        }
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        ModuleWizardStep[] previousWizardSteps = super.createWizardSteps(wizardContext, modulesProvider);
        ModuleWizardStep[] wizardSteps = new ModuleWizardStep[previousWizardSteps.length+1];

        wizardSteps[0] = new StepicModuleWizardStep(getGenerator(), wizardContext);
//        wizardSteps[0] = new StudyNewProjectPanel(this, wizardContext);
        for (int i = 0; i < previousWizardSteps.length; i++) {
            wizardSteps[i+1] = previousWizardSteps[i];
        }

        return wizardSteps;
    }

    private EduProjectGenerator getGenerator(){
        if (generator == null){
            generator = new EduProjectGenerator();
        }
        return generator;
    }
}