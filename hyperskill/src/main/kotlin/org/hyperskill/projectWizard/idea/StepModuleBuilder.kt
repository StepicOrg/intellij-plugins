package org.hyperskill.projectWizard.idea

import com.intellij.ide.highlighter.ModuleFileType
import com.intellij.openapi.util.io.FileUtil
import org.stepik.core.courseFormat.StepNode

class StepModuleBuilder(moduleDir: String, stepNode: StepNode) : ModuleBuilderWithSrc() {
    init {
        val stepName = stepNode.directory
        name = stepName
        moduleFilePath = FileUtil.join(moduleDir, stepName,
                stepName + ModuleFileType.DOT_DEFAULT_EXTENSION)
    }
}
