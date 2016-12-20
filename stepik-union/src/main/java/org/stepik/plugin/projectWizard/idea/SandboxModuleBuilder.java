package org.stepik.plugin.projectWizard.idea;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.core.EduNames;
import org.jetbrains.annotations.NotNull;

class SandboxModuleBuilder extends ModuleBuilderWithSrc {
    SandboxModuleBuilder(@NotNull String moduleDir) {
        super();
        setName(EduNames.SANDBOX_DIR);
        setModuleFilePath(FileUtil.join(moduleDir,
                EduNames.SANDBOX_DIR,
                EduNames.SANDBOX_DIR + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }
}