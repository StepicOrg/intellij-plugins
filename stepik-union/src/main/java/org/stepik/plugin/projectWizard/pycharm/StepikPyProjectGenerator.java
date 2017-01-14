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
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.SectionNode;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import icons.AllStepikIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.courses.Course;
import org.stepik.plugin.projectWizard.StepikProjectGenerator;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


class StepikPyProjectGenerator extends PythonProjectGenerator<PyNewProjectSettings> {
    private static final Logger logger = Logger.getInstance(StepikPyProjectGenerator.class.getName());
    private static final String MODULE_NAME = "Stepik";
    private final StepikProjectGenerator generator;
    private final PyCharmWizardStep wizardStep;

    private StepikPyProjectGenerator() {
        super(true);
        generator = StepikProjectGenerator.getInstance();
        Project defaultProject = DefaultProjectFactory.getInstance().getDefaultProject();
        wizardStep = new PyCharmWizardStep(this, defaultProject);
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
        wizardStep.updateStep();
        return wizardStep.getComponent();
    }

    @NotNull
    @Override
    public ValidationResult validate(@NotNull String s) {
        return wizardStep.check();
    }

    @Nullable
    @Override
    public BooleanFunction<PythonProjectGenerator> beforeProjectGenerated(@Nullable Sdk sdk) {
        return generator -> {
            Project defaultProject = DefaultProjectFactory.getInstance().getDefaultProject();
            StepikConnectorLogin.loginFromDialog(defaultProject);
            final Course course = wizardStep.getSelectedCourse();
            if (course.getId() == 0) {
                return false;
            }

            this.generator.setSelectedCourse(course);
            StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();
            stepikApiClient.enrollments()
                    .post()
                    .course(course.getId())
                    .execute();
            return true;
        };
    }

    @Override
    public Object getProjectSettings() {
        return new PyNewProjectSettings();
    }

    @Override
    public void configureProject(
            @NotNull final Project project,
            @NotNull VirtualFile baseDir,
            @NotNull final PyNewProjectSettings settings,
            @NotNull final Module module,
            @Nullable final PyProjectSynchronizer synchronizer) {
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
        CourseNode courseNode = stepManager.getCourseNode();
        if (courseNode == null) {
            logger.warn("failed to generate builders");
            return;
        }

        FileUtil.createDirectory(new File(project.getBasePath(), "Sandbox"));

        createSubDirectories(courseNode, project);

        ApplicationManager.getApplication().invokeLater(
                () -> DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND,
                        () -> ApplicationManager.getApplication().runWriteAction(
                                () -> StudyProjectComponent.getInstance(project)
                                        .registerStudyToolWindow(courseNode))));
    }

    private void createSubDirectories(
            @NotNull CourseNode courseNode,
            @NotNull Project project) {
        for (SectionNode sectionNode : courseNode.getSectionNodes()) {
            FileUtil.createDirectory(new File(project.getBasePath(), sectionNode.getPath()));
            for (LessonNode lessonNode : sectionNode.getLessonNodes()) {
                FileUtil.createDirectory(new File(project.getBasePath(), lessonNode.getPath()));
                for (StepNode stepNode : lessonNode.getStepNodes()) {
                    stepNode.setCurrentLang(SupportedLanguages.PYTHON);
                    File stepDir = new File(project.getBasePath(), stepNode.getPath());
                    File srcDir = new File(stepDir, "src");
                    FileUtil.createDirectory(stepDir);
                    FileUtil.createDirectory(srcDir);

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(srcDir, "main.py")))) {
                        StepFile stepFile = stepNode.getFile("main.py");
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
