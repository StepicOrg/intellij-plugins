package org.stepik.from.edu.intellij.utils.generation;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.core.EduNames;

public class JavaSandboxModuleBuilder extends JavaModuleBuilder {
    public JavaSandboxModuleBuilder(String moduleDir) {
        super();
        setName(EduNames.SANDBOX_DIR);
        setModuleFilePath(FileUtil.join(moduleDir,
                EduNames.SANDBOX_DIR,
                EduNames.SANDBOX_DIR + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }
}