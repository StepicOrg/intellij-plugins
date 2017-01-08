package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.facet.ui.FacetEditorValidator;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.DefaultProjectFactory;
import com.intellij.openapi.project.DumbModePermission;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.BooleanFunction;
import com.jetbrains.python.newProject.PyNewProjectSettings;
import com.jetbrains.python.newProject.PythonProjectGenerator;
import com.jetbrains.python.remote.PyProjectSynchronizer;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyProjectComponent;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.Step;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import icons.AllStepikIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.courses.Course;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


class StepikPyProjectGenerator extends PythonProjectGenerator<PyNewProjectSettings> {
    private static final Logger logger = Logger.getInstance(StepikPyProjectGenerator.class.getName());
    private static final String MODULE_NAME = "Stepik";
    private final StepikProjectGenerator generator;
    private final PyCCSettingPanel pySPanel;

    private StepikPyProjectGenerator() {
        super(true);
        generator = StepikProjectGenerator.getInstance();
        pySPanel = new PyCCSettingPanel();
    }

    @Nullable
    @Override
    public Icon getLogo() {
        return AllStepikIcons.stepikLogo;
    }

    @NotNull
    @Nls
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Nullable
    @Override
    public JPanel extendBasePanel() throws ProcessCanceledException {
        Project defaultProject = DefaultProjectFactory.getInstance().getDefaultProject();
        StepikConnectorLogin.loginFromDialog(defaultProject);
        pySPanel.init(defaultProject);
        return pySPanel.getMainPanel();
    }

    @NotNull
    @Override
    public ValidationResult validate(@NotNull String s) {
        return pySPanel.check();
    }

    @Nullable
    @Override
    public BooleanFunction<PythonProjectGenerator> beforeProjectGenerated(@Nullable Sdk sdk) {
        return generator -> {
            Project defaultProject = DefaultProjectFactory.getInstance().getDefaultProject();
            StepikConnectorLogin.loginFromDialog(defaultProject);
            final Course course = pySPanel.getSelectedCourse();
            if (course == null || course == StepikProjectGenerator.EMPTY_COURSE) return false;
            this.generator.setSelectedCourse(course);
            if (PyCCSettingPanel.COURSE_LINK.equals(pySPanel.getBuildType())) {
                StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();
                stepikApiClient.enrollments()
                        .post()
                        .course(course.getId())
                        .execute();
            }
            StepikProjectGenerator.downloadAndFlushCourse(defaultProject, course.getId());
            return true;
        };
    }

    @Override
    public Object getProjectSettings() {
        return new PyNewProjectSettings();
    }

    @Override
    public void configureProject(
            @NotNull final Project project, @NotNull VirtualFile baseDir, @NotNull final PyNewProjectSettings settings,
            @NotNull final Module module, @Nullable final PyProjectSynchronizer synchronizer) {
        super.configureProject(project, baseDir, settings, module, synchronizer);
        StepikConnectorLogin.loginFromDialog(project);
        ApplicationManager.getApplication()
                .runWriteAction(() -> ModuleRootModificationUtil.setModuleSdk(module, settings.getSdk()));
        createCourseFromGenerator(project);
    }

    private void createCourseFromGenerator(
            @NotNull Project project) {
        generator.generateProject(project);

        StepikProjectManager stepManager = StepikProjectManager.getInstance(project);
        stepManager.setDefaultLang(generator.getDefaultLang());
        com.jetbrains.tmp.learning.courseFormat.Course course = stepManager.getCourse();
        if (course == null) {
            logger.warn("failed to generate builders");
            return;
        }

        FileUtil.createDirectory(new File(project.getBasePath(), "Sandbox"));

        createSubDirectories(course, project);

        ApplicationManager.getApplication().invokeLater(
                () -> DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND,
                        () -> ApplicationManager.getApplication().runWriteAction(
                                () -> StudyProjectComponent.getInstance(project)
                                        .registerStudyToolWindow(course))));
    }

    private void createSubDirectories(
            @NotNull com.jetbrains.tmp.learning.courseFormat.Course course,
            @NotNull Project project) {
        for (Section section : course.getSections()) {
            FileUtil.createDirectory(new File(project.getBasePath(), section.getPath()));
            for (Lesson lesson : section.getLessons()) {
                FileUtil.createDirectory(new File(project.getBasePath(), lesson.getPath()));
                for (Step step : lesson.getSteps()) {
                    step.setCurrentLang(SupportedLanguages.PYTHON);
                    File stepDir = new File(project.getBasePath(), step.getPath());
                    File srcDir = new File(stepDir, "src");
                    FileUtil.createDirectory(stepDir);
                    FileUtil.createDirectory(srcDir);

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(srcDir, "main.py")))) {
                        StepFile stepFile = step.getFile("main.py");
                        if (stepFile != null) {
                            writer.write(stepFile.getText());
                        }
                    } catch (IOException e) {
                        logger.warn(e);
                    }
                }
            }
        }
    }
}
