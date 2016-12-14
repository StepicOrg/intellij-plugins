package org.stepik.plugin.projectWizard.pycharm;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.DefaultProjectFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.newProject.PyNewProjectSettings;
import com.jetbrains.python.newProject.PythonProjectGenerator;
import com.jetbrains.python.remote.PyProjectSynchronizer;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.from.edu.intellij.utils.generation.SelectCourseWizardStep;

import javax.swing.*;
import java.io.File;


public class StepikPyProjectGenerator extends PythonProjectGenerator<PyNewProjectSettings> {
    private static final Logger LOG = Logger.getInstance(StepikPyProjectGenerator.class.getName());
    private final StepikProjectGenerator myGenerator;
    private static final String NO_PYTHON_INTERPRETER = "<html><u>Add</u> python interpreter.</html>";
    public ValidationResult myValidationResult = new ValidationResult("selected course is not valid");
    private SelectCourseWizardStep courseWizardStep;

    public StepikPyProjectGenerator() {
        super(true);
        myGenerator = new StepikProjectGenerator();
        courseWizardStep = new SelectCourseWizardStep(myGenerator, DefaultProjectFactory.getInstance().getDefaultProject() );
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
//        return null;
    }

    @Override
    public Object getProjectSettings() {
        return new PyNewProjectSettings();
    }

    @Nullable
    @Override
    public Icon getLogo() {
        return IconLoader.getIcon("/icons/stepik_logotype_13x13-2.png");
//        return null;
    }

    @Override
    public void configureProject(@NotNull final Project project, @NotNull VirtualFile baseDir, @NotNull final PyNewProjectSettings settings,
            @NotNull final Module module, @Nullable final PyProjectSynchronizer synchronizer) {
        // Super should be called according to its contract unless we sync project explicitly (we do not, so we call super)
        super.configureProject(project, baseDir, settings, module, synchronizer);
        ApplicationManager.getApplication().runWriteAction(() -> ModuleRootModificationUtil.setModuleSdk(module, settings.getSdk()));
    }

    @NotNull
    @Override
    public ValidationResult validate(@NotNull String baseDirPath) {
    /*if (PythonSdkType.isRemote(myProjectAction.getSdk())) {
      if (PythonRemoteInterpreterManager.getInstance() == null) {
        return new ValidationResult(PythonRemoteInterpreterManager.WEB_DEPLOYMENT_PLUGIN_IS_DISABLED);
      }
    }*/
        return ValidationResult.OK;
    }
}
