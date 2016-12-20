package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.DefaultProjectFactory;
import com.intellij.openapi.project.DumbModePermission;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.newProject.PyNewProjectSettings;
import com.jetbrains.python.newProject.PythonProjectGenerator;
import com.jetbrains.python.remote.PyProjectSynchronizer;
import com.jetbrains.tmp.learning.StudyProjectComponent;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class StepikPyProjectGenerator extends PythonProjectGenerator<PyNewProjectSettings> {
    private static final Logger logger = Logger.getInstance(StepikPyProjectGenerator.class.getName());
    private final StepikProjectGenerator generator;
    private SelectCourseWizardStep courseWizardStep;

    public StepikPyProjectGenerator() {
        super(true);
        generator = StepikProjectGenerator.getInstance();
        courseWizardStep = new SelectCourseWizardStep(generator,
                DefaultProjectFactory.getInstance().getDefaultProject());
        courseWizardStep.setupPyCharmSetting();
    }

    @NotNull
    @Nls
    @Override
    public String getName() {
        return "Stepik Union";
    }

    @Override
    @Nullable
    public JComponent getSettingsPanel(File baseDir) throws ProcessCanceledException {
        return courseWizardStep.getComponent();
    }

    @Override
    public Object getProjectSettings() {
        return new PyNewProjectSettings();
    }

    @Nullable
    @Override
    public Icon getLogo() {
        return IconLoader.getIcon("/icons/stepik_logotype_13x13-2.png");
    }

    @NotNull
    @Override
    public ValidationResult validate(@NotNull String s) {
        return ValidationResult.OK;
    }

    @Override
    public void configureProject(
            @NotNull final Project project, @NotNull VirtualFile baseDir, @NotNull final PyNewProjectSettings settings,
            @NotNull final Module module, @Nullable final PyProjectSynchronizer synchronizer) {
        // Super should be called according to its contract unless we sync project explicitly (we do not, so we call super)
        super.configureProject(project, baseDir, settings, module, synchronizer);
        ApplicationManager.getApplication()
                .runWriteAction(() -> ModuleRootModificationUtil.setModuleSdk(module, settings.getSdk()));
        courseWizardStep.onStepLeaving();
        StepikProjectGenerator.downloadAndFlushCourse(project, courseWizardStep.getSelectedCourse());
        createCourseFromGenerator(project);
    }

    private void createCourseFromGenerator(
            @NotNull Project project) {
        generator.generateProject(project);

        StudyTaskManager taskManager = StudyTaskManager.getInstance(project);
        taskManager.setDefaultLang(generator.getDefaultLang());
        Course course = taskManager.getCourse();
        if (course == null) {
            logger.warn("failed to generate builders");
            return;
        }
        course.setCourseMode(EduNames.STEPIK_CODE);

        logger.info("Module dir = " + new File(project.getBasePath(), "Sandbox").getAbsolutePath());
//        new SandboxModuleBuilder(moduleDir).createModule(moduleModel);
        FileUtil.createDirectory(new File(project.getBasePath(), "Sandbox"));

        createSubDirectories(course, project);

        ApplicationManager.getApplication().invokeLater(
                () -> DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND,
                        () -> ApplicationManager.getApplication().runWriteAction(
                                () -> StudyProjectComponent.getInstance(project)
                                        .registerStudyToolWindow(course))));
    }

    private void createSubDirectories(
            @NotNull Course course,
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
                for (Task task : lesson.getTaskList()) {
                    task.setIndex(taskIndex++);
                    logger.info("task Path = " + task.getPath());
                    File taskDir = new File(project.getBasePath(), task.getPath());
                    FileUtil.createDirectory(taskDir);

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(taskDir, "main.py")))) {
                        writer.write(task.getFile("main.py").getText());
                    } catch (IOException e) {
                        logger.warn(e);
                    }
                }
            }
        }
    }
}
