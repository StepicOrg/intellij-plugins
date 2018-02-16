package org.stepik.core.projectWizard.idea

import com.intellij.ide.highlighter.ModuleFileType
import com.intellij.openapi.util.io.FileUtil
import org.stepik.core.core.EduNames

class SandboxModuleBuilder(moduleDir: String) : ModuleBuilderWithSrc() {
    init {
        name = EduNames.SANDBOX_DIR
        moduleFilePath = FileUtil.join(moduleDir,
                EduNames.SANDBOX_DIR,
                EduNames.SANDBOX_DIR + ModuleFileType.DOT_DEFAULT_EXTENSION)
    }
}
