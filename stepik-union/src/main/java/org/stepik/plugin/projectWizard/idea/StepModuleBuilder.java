package org.stepik.plugin.projectWizard.idea;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.util.io.FileUtil;
import org.stepik.core.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;

public class StepModuleBuilder extends ModuleBuilderWithSrc {
    public StepModuleBuilder(String moduleDir, @NotNull StepNode stepNode) {
        String stepName = stepNode.getDirectory();
        setName(stepName);
        setModuleFilePath(FileUtil.join(moduleDir, stepName,
                stepName + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }
}
