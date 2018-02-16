package org.stepik.core.projectWizard.idea

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleWithNameAlreadyExists
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.DefaultProjectFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.vfs.VirtualFileManager
import org.jdom.JDOMException
import org.stepik.core.StepikProjectManager
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.projectWizard.ProjectWizardUtils.createSubDirectories
import org.stepik.core.projectWizard.StepikProjectGenerator
import org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory
import java.io.IOException

class CourseModuleBuilder : AbstractModuleBuilder() {
    private val generator = StepikProjectGenerator
    internal var wizardStep: JavaWizardStep? = null

    @Throws(InvalidDataException::class,
            IOException::class,
            ModuleWithNameAlreadyExists::class,
            JDOMException::class,
            ConfigurationException::class)
    override fun createModule(moduleModel: ModifiableModuleModel): Module {
        val baseModule = super.createModule(moduleModel)
        val project = baseModule.project
        logger.info("Create project module")
        createCourseFromGenerator(moduleModel, project)
        return baseModule
    }

    @Throws(InvalidDataException::class,
            IOException::class,
            ModuleWithNameAlreadyExists::class,
            JDOMException::class,
            ConfigurationException::class)
    private fun createCourseFromGenerator(
            moduleModel: ModifiableModuleModel,
            project: Project) {
        generator.generateProject(project)

        val moduleDir = moduleFileDirectory ?: return

        logger.info("Module dir = $moduleDir")
        SandboxModuleBuilder(moduleDir).createModule(moduleModel)

        val root = StepikProjectManager.getProjectRoot(project)
        if (root == null) {
            logger.info("Failed to generate builders: project root is null")
            return
        }

        if (root is StepNode) {
            getOrCreateSrcDirectory(project, (root as StepNode?)!!, true, moduleModel)
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
            wizardContext.project

        wizardStep = JavaWizardStep(generator, project!!)
        wizardSteps[0] = wizardStep

        return wizardSteps
    }

    companion object {
        private val logger = Logger.getInstance(CourseModuleBuilder::class.java)
    }
}
