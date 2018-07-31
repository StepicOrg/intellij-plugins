package org.stepik.hyperskill.projectWizard.idea

import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import org.stepik.core.projectWizard.idea.BaseModuleBuilder

abstract class AbstractModuleBuilder : BaseModuleBuilder() {
    override fun getModuleType() = StepikModuleType.STEPIK_MODULE_TYPE
    
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
