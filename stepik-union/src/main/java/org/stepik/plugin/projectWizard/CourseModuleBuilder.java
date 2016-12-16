package org.stepik.plugin.projectWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DefaultProjectFactory;
import com.intellij.openapi.project.DumbModePermission;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.InvalidDataException;
import com.jetbrains.tmp.learning.StudyProjectComponent;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.stepik.from.edu.intellij.utils.generation.SelectCourseWizardStep;

import java.io.IOException;

class CourseModuleBuilder extends AbstractModuleBuilder {
    private static final Logger logger = Logger.getInstance(CourseModuleBuilder.class);
    private StepikProjectGenerator generator = StepikProjectGenerator.getInstance();

    private void createCourseFromGenerator(
            @NotNull ModifiableModuleModel moduleModel,
            @NotNull Project project,
            @NotNull StepikProjectGenerator generator)
            throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        generator.generateProject(project, project.getBaseDir());

        StudyTaskManager taskManager = StudyTaskManager.getInstance(project);
        taskManager.setDefaultLang(this.generator.getDefaultLang());
        Course course = taskManager.getCourse();
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
        new SandboxModuleBuilder(moduleDir).createModule(moduleModel);

        createLessonModules(moduleModel, course, moduleDir, project);

        ApplicationManager.getApplication().invokeLater(
                () -> DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND,
                        () -> ApplicationManager.getApplication().runWriteAction(
                                () -> StudyProjectComponent.getInstance(project)
                                        .registerStudyToolWindow(course))));
    }

    private void createLessonModules(
            @NotNull ModifiableModuleModel moduleModel,
            @NotNull Course course,
            @NotNull String moduleDir,
            @NotNull Project project
    ) throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        int sectionIndex = 0;
        int lessonIndex = 1;
        for (Section section : course.getSections()) {
            section.setIndex(++sectionIndex);
            SectionModuleBuilder sectionBuilder = new SectionModuleBuilder(moduleDir, section);
            sectionBuilder.createModule(moduleModel);
            for (Lesson lesson : section.getLessons()) {
                lesson.setIndex(lessonIndex++);
                String sectionDir = moduleDir + "/" + section.getDirectory();
                LessonModuleBuilder lessonBuilder = new LessonModuleBuilder(sectionDir, lesson, project);
                lessonBuilder.createModule(moduleModel);
            }
        }
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

        Project project = wizardContext.getProject() == null ?
                DefaultProjectFactory.getInstance().getDefaultProject() :
                wizardContext.getProject();
        wizardSteps[0] = new SelectCourseWizardStep(getGenerator(), project);

        return wizardSteps;
    }

    private StepikProjectGenerator getGenerator() {
        return StepikProjectGenerator.getInstance();
    }
}