package org.stepik.core.projectWizard.idea

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel


abstract class AbstractModuleBuilder : ModuleBuilder() {
    override fun getModuleType() = StepikModuleType.STEPIK_MODULE_TYPE

    @Throws(ConfigurationException::class)
    override fun setupRootModel(rootModel: ModifiableRootModel) {
        val compilerModuleExtension = rootModel.getModuleExtension(CompilerModuleExtension::class.java)
        compilerModuleExtension.isExcludeOutput = true
        if (this.myJdk != null) {
            rootModel.sdk = this.myJdk
        } else {
            rootModel.inheritSdk()
        }

        compilerModuleExtension.inheritCompilerOutputPath(true)

        doAddContentEntry(rootModel)
    }

    override fun getPresentableName() = StepikModuleType.MODULE_NAME
}
