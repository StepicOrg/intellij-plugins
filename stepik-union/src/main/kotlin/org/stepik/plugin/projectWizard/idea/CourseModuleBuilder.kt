package org.stepik.plugin.projectWizard.idea

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.DefaultProjectFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.vfs.VirtualFileManager
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.projectWizard.ProjectWizardUtils.createSubDirectories
import org.stepik.core.utils.getOrCreateSrcDirectory
import org.stepik.plugin.projectWizard.StepikProjectGenerator

class CourseModuleBuilder : AbstractModuleBuilder() {
    private val generator = StepikProjectGenerator
    internal var wizardStep: JavaWizardStep? = null

    override fun createModule(moduleModel: ModifiableModuleModel): Module {
        val baseModule = super.createModule(moduleModel)
        logger.info("Create project module")
        createCourseFromGenerator(moduleModel, baseModule.project)
        return baseModule
    }

    private fun createCourseFromGenerator(
            moduleModel: ModifiableModuleModel,
            project: Project) {
        generator.generateProject(project)

        val moduleDir = moduleFileDirectory ?: return

        logger.info("Module dir = $moduleDir")
        SandboxModuleBuilder(moduleDir).createModule(moduleModel)

        val root = getProjectManager(project)?.projectRoot
        if (root == null) {
            logger.info("Failed to generate builders: project root is null")
            return
        }

        if (root is StepNode) {
            getOrCreateSrcDirectory(project, root, true, moduleModel)
        } else {
            createSubDirectories(project, generator.defaultLang, root, moduleModel)
            VirtualFileManager.getInstance().syncRefresh()
        }
    }

    override fun createWizardSteps(
            wizardContext: WizardContext,
            modulesProvider: ModulesProvider): Array<ModuleWizardStep?> {
        val previousWizardSteps = super.createWizardSteps(wizardContext, modulesProvider)
        val wizardSteps = arrayOfNulls<ModuleWizardStep>(previousWizardSteps.size + 1)

        val project = if (wizardContext.project == null)
            DefaultProjectFactory.getInstance().defaultProject
        else
            wizardContext.project!!

        wizardStep = JavaWizardStep(generator, project)
        wizardSteps[0] = wizardStep

        return wizardSteps
    }
}
