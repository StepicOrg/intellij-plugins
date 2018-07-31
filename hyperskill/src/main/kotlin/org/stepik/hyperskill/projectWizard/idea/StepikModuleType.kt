package org.stepik.hyperskill.projectWizard.idea

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.CommonClassNames
import com.intellij.psi.JavaPsiFacade
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes
import org.stepik.core.icons.AllStepikIcons

class StepikModuleType : ModuleType<CourseModuleBuilder>(ID) {
    
    override fun createModuleBuilder() = CourseModuleBuilder()
    
    override fun getName() = MODULE_NAME
    
    override fun getDescription() = "Stepik Module Type"
    
    override fun getBigIcon() = AllStepikIcons.stepikLogoBig
    
    override fun getNodeIcon(b: Boolean) = AllStepikIcons.stepikLogo
    
    override fun modifyProjectTypeStep(
            settingsStep: SettingsStep,
            moduleBuilder: ModuleBuilder): ModuleWizardStep? {
        return ProjectWizardStepFactory.getInstance()
                .createJavaSettingsStep(settingsStep, moduleBuilder, { moduleBuilder.isSuitableSdkType(it) })
    }
    
    override fun isValidSdk(module: Module, projectSdk: Sdk?) = isValidJavaSdk(module)
    
    companion object {
        internal const val MODULE_NAME = "Stepik"
        internal val STEPIK_MODULE_TYPE: StepikModuleType = instantiate()
        private const val ID = "STEPIK_MODULE_TYPE"
        
        private fun instantiate(): StepikModuleType {
            try {
                return Class.forName("org.stepik.hyperskill.projectWizard.idea.StepikModuleType")
                        .newInstance() as StepikModuleType
            } catch (e: Exception) {
                throw IllegalArgumentException(e)
            }
        }
        
        private fun isValidJavaSdk(module: Module): Boolean {
            val moduleRootManager = ModuleRootManager.getInstance(module)
            return if (moduleRootManager.getSourceRoots(JavaModuleSourceRootTypes.SOURCES).isEmpty()) {
                true
            } else {
                val javaPsiFacade = JavaPsiFacade.getInstance(module.project)
                val clazz = javaPsiFacade.findClass(CommonClassNames.JAVA_LANG_OBJECT, module.moduleWithLibrariesScope)
                clazz != null
            }
        }
    }
}
