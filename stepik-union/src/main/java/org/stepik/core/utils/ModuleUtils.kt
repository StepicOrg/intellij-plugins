package org.stepik.core.utils

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.ModuleWithNameAlreadyExists
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import org.jdom.JDOMException
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.projectWizard.idea.StepModuleBuilder
import java.io.IOException


internal object ModuleUtils {
    private val logger = Logger.getInstance(ModuleUtils::class.java)

    fun createStepModule(
            project: Project,
            step: StepNode,
            moduleModel: ModifiableModuleModel) {
        val lesson = step.parent
        if (lesson != null) {
            val moduleDir = arrayOf(project.basePath, lesson.path).joinToString("/")
            val stepModuleBuilder = StepModuleBuilder(moduleDir, step)
            try {
                stepModuleBuilder.createModule(moduleModel)
            } catch (e: IOException) {
                logger.warn("Cannot create step: ${step.directory}", e)
            } catch (e: ModuleWithNameAlreadyExists) {
                logger.warn("Cannot create step: ${step.directory}", e)
            } catch (e: JDOMException) {
                logger.warn("Cannot create step: ${step.directory}", e)
            } catch (e: ConfigurationException) {
                logger.warn("Cannot create step: ${step.directory}", e)
            }

        }
    }
}
