package org.stepik.plugin.projectWizard.idea;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.stepik.core.projectWizard.ProjectWizardUtils.createStepDirectory;

public class StepModuleBuilder extends ModuleBuilderWithSrc {
    private final StepNode stepNode;
    private final Project project;

    public StepModuleBuilder(String moduleDir, @NotNull StepNode stepNode, @NotNull Project project) {
        this.stepNode = stepNode;
        this.project = project;
        String stepName = stepNode.getDirectory();
        setName(stepName);
        setModuleFilePath(FileUtil.join(moduleDir, stepName,
                stepName + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException,
            IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module module = super.createModule(moduleModel);
        createStepDirectory(project, stepNode);

        return module;
    }
}
