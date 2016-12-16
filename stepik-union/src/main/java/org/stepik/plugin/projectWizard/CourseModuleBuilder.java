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
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.StudyProjectComponent;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.stepik.from.edu.intellij.utils.generation.SelectCourseWizardStep;

import java.io.File;
import java.io.IOException;

class CourseModuleBuilder extends AbstractModuleBuilder {
    private static final Logger logger = Logger.getInstance(CourseModuleBuilder.class);
    private StepikProjectGenerator generator = StepikProjectGenerator.getInstance();

    private void createCourseFromGenerator(
            @NotNull ModifiableModuleModel moduleModel,
            @NotNull Project project)
            throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        generator.generateProject(project, project.getBaseDir());

        StudyTaskManager taskManager = StudyTaskManager.getInstance(project);
        taskManager.setDefaultLang(generator.getDefaultLang());
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

        createSubDirectories(course, moduleModel, project);

        ApplicationManager.getApplication().invokeLater(
                () -> DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND,
                        () -> ApplicationManager.getApplication().runWriteAction(
                                () -> StudyProjectComponent.getInstance(project)
                                        .registerStudyToolWindow(course))));
    }

    private void createSubDirectories(
            @NotNull Course course,
            @NotNull ModifiableModuleModel moduleModel,
            @NotNull Project project) {
        int sectionIndex = 0;
        int lessonIndex = 1;
        for (Section section : course.getSections()) {
            section.setIndex(++sectionIndex);
            FileUtil.createDirectory(new File(project.getBasePath(), section.getPath()));
            for (Lesson lesson : section.getLessons()) {
                lesson.setIndex(lessonIndex++);
                FileUtil.createDirectory(new File(project.getBasePath(), lesson.getPath()));
                int taskIndex = 1;
                for (Task task : lesson.getTaskList()){
                    task.setIndex(taskIndex++);
                    logger.info("task Path = " + task.getPath());
                    TaskModuleBuilder taskModuleBuilder = new TaskModuleBuilder(project.getBasePath() + lesson.getPath(),
                            lesson.getDirectory(),
                            task,
                            project);
                    try {
                        taskModuleBuilder.createModule(moduleModel);
                    } catch (IOException | ModuleWithNameAlreadyExists | JDOMException | ConfigurationException e) {
                        logger.warn("Cannot create task: " + task.getDirectory(), e);
                    }
                }
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
        createCourseFromGenerator(moduleModel, project);
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
        wizardSteps[0] = new SelectCourseWizardStep(generator, project);

        return wizardSteps;
    }
}