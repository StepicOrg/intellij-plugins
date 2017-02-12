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
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.BooleanFunction;
import com.jetbrains.python.newProject.PyNewProjectSettings;
import com.jetbrains.python.newProject.PythonProjectGenerator;
import com.jetbrains.python.remote.PyProjectSynchronizer;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyProjectComponent;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import icons.AllStepikIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.StudyObject;
import org.stepik.plugin.projectWizard.ProjectWizardUtils;
import org.stepik.plugin.projectWizard.StepikProjectGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.jetbrains.tmp.learning.SupportedLanguages.PYTHON3;


class StepikPyProjectGenerator extends PythonProjectGenerator<PyNewProjectSettings> {
    private static final Logger logger = Logger.getInstance(StepikPyProjectGenerator.class);
    private static final String MODULE_NAME = "Stepik";
    private final StepikProjectGenerator generator;
    private final PyCharmWizardStep wizardStep;
    private final Project project;
    private TextFieldWithBrowseButton locationField;
    private boolean locationSetting;
    private boolean keepLocation;

    private StepikPyProjectGenerator() {
        super(true);
        generator = StepikProjectGenerator.getInstance();
        this.project = DefaultProjectFactory.getInstance().getDefaultProject();
        wizardStep = new PyCharmWizardStep(this, project);
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
        locationField = null;
        keepLocation = false;
        wizardStep.updateStep();
        return wizardStep.getComponent();
    }

    @NotNull
    private String getLocation() {
        TextFieldWithBrowseButton locationField = getLocationField();

        if (locationField == null) {
            return "";
        }

        return locationField.getText();
    }

    private void setLocation(@NotNull String location) {
        if (keepLocation) {
            return;
        }

        TextFieldWithBrowseButton locationField = getLocationField();

        if (locationField == null) {
            return;
        }

        locationSetting = true;
        locationField.setText(location);
        locationSetting = false;
    }

    @Nullable
    private TextFieldWithBrowseButton getLocationField() {
        if (locationField == null) {
            Container basePanel = wizardStep.getComponent().getParent();
            if (basePanel == null) {
                return null;
            }
            try {
                Container topPanel = (Container) basePanel.getComponent(0);
                LabeledComponent locationComponent = (LabeledComponent) topPanel.getComponent(0);
                locationField = (TextFieldWithBrowseButton) locationComponent.getComponent();
            } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
                logger.warn("Auto naming for a project don't work: ", e);
                return null;
            }
        }

        return locationField;
    }

    @Override
    public void locationChanged(@NotNull String newLocation) {
        keepLocation = keepLocation || !locationSetting;
    }

    @Override
    public void fireStateChanged() {
        if (!keepLocation && getLocationField() != null) {
            long id = wizardStep.getSelectedCourse().getId();
            String projectName = "course" + id;
            String projectDir = new File(getLocation()).getParent();
            projectName = ProjectWizardUtils.findNonExistingFileName(projectDir, projectName);
            setLocation(projectDir + "/" + projectName);
        }

        super.fireStateChanged();
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
            final StudyObject course = wizardStep.getSelectedCourse();
            if (course.getId() == 0) {
                return false;
            }

            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
            stepikApiClient.enrollments()
                    .post()
                    .course(course.getId())
                    .execute();
            this.generator.createCourseNodeUnderProgress(project, course);
            return true;
        };
    }

    @Override
    public void configureProject(
            @NotNull final Project project,
            @NotNull VirtualFile baseDir,
            @NotNull final PyNewProjectSettings settings,
            @NotNull final Module module,
            @Nullable final PyProjectSynchronizer synchronizer) {
        super.configureProject(project, baseDir, settings, module, synchronizer);
        ApplicationManager.getApplication()
                .runWriteAction(() -> ModuleRootModificationUtil.setModuleSdk(module, settings.getSdk()));
        createCourseFromGenerator(project);
    }

    private void createCourseFromGenerator(
            @NotNull Project project) {
        generator.generateProject(project);

        FileUtil.createDirectory(new File(project.getBasePath(), "Sandbox"));

        StepikProjectManager projectManager = StepikProjectManager.getInstance(project);
        if (projectManager == null) {
            logger.warn("failed to generate builders: StepikProjectManager is null");
            return;
        }
        projectManager.setDefaultLang(generator.getDefaultLang());
        StudyNode root = projectManager.getProjectRoot();
        if (root == null) {
            logger.warn("failed to generate builders: Root is null");
            return;
        }

        createSubDirectories(root, project);

        ApplicationManager.getApplication().invokeLater(
                () -> DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND,
                        () -> ApplicationManager.getApplication().runWriteAction(
                                () -> StudyProjectComponent.getInstance(project)
                                        .registerStudyToolWindow())));
    }

    private void createSubDirectories(
            @NotNull StudyNode<?, ?> root,
            @NotNull Project project) {
        root.getChildren()
                .forEach(child -> {
                    FileUtil.createDirectory(new File(project.getBasePath(), child.getPath()));
                    if (child instanceof StepNode) {
                        StepNode stepNode = (StepNode) child;
                        stepNode.setCurrentLang(PYTHON3);
                        File stepDir = new File(project.getBasePath(), stepNode.getPath());
                        File srcDir = new File(stepDir, "src");
                        FileUtil.createDirectory(stepDir);
                        FileUtil.createDirectory(srcDir);

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(srcDir, "main.py")))) {
                            String template = stepNode.getCurrentTemplate();
                            writer.write(template);
                        } catch (IOException e) {
                            logger.warn(e);
                        }
                    } else {
                        createSubDirectories(child, project);
                    }
                });
    }
}
