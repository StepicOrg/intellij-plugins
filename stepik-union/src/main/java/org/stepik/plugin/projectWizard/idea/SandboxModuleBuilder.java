package org.stepik.plugin.projectWizard.idea;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.util.io.FileUtil;
import org.stepik.core.core.EduNames;
import org.jetbrains.annotations.NotNull;

public class SandboxModuleBuilder extends ModuleBuilderWithSrc {
    public SandboxModuleBuilder(@NotNull String moduleDir) {
        super();
        setName(EduNames.SANDBOX_DIR);
        setModuleFilePath(FileUtil.join(moduleDir,
                EduNames.SANDBOX_DIR,
                EduNames.SANDBOX_DIR + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }
}