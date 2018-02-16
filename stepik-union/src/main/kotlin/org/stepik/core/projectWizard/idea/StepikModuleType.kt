package org.stepik.core.projectWizard.idea

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
import org.stepik.core.projectWizard.ProjectWizardUtils
import javax.swing.Icon

class StepikModuleType : ModuleType<CourseModuleBuilder>(ID) {

    override fun createModuleBuilder(): CourseModuleBuilder {
        return CourseModuleBuilder()
    }

    override fun getName() = MODULE_NAME

    override fun getDescription() = "Stepik Module Type"

    override fun getBigIcon(): Icon {
        return AllStepikIcons.stepikLogoBig
    }

    override fun getNodeIcon(b: Boolean): Icon {
        return AllStepikIcons.stepikLogo
    }

    override fun modifyProjectTypeStep(
            settingsStep: SettingsStep,
            moduleBuilder: ModuleBuilder): ModuleWizardStep? {
        return ProjectWizardStepFactory.getInstance()
                .createJavaSettingsStep(settingsStep, moduleBuilder, { moduleBuilder.isSuitableSdkType(it) })
    }

    override fun isValidSdk(module: Module, projectSdk: Sdk?): Boolean {
        return isValidJavaSdk(module)
    }

    override fun modifySettingsStep(
            settingsStep: SettingsStep, moduleBuilder: ModuleBuilder): ModuleWizardStep? {
        val nameField = settingsStep.moduleNameField
        if (nameField != null) {
            val courseModuleBuilder = moduleBuilder as CourseModuleBuilder
            val studyObject = courseModuleBuilder.wizardStep?.selectedStudyObject
            val projectDirectory = settingsStep.context.projectFileDirectory
            val projectName = ProjectWizardUtils.getProjectDefaultName(projectDirectory, studyObject!!)
            nameField.text = projectName
        }
        return null
    }

    companion object {
        internal const val MODULE_NAME = "Stepik"
        internal val STEPIK_MODULE_TYPE: StepikModuleType
        private const val ID = "STEPIK_MODULE_TYPE"

        init {
            STEPIK_MODULE_TYPE = instantiate()
        }

        private fun instantiate(): StepikModuleType {
            try {
                return Class.forName("org.stepik.core.projectWizard.idea.StepikModuleType")
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
