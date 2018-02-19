package org.stepik.core.utils

import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.project.Project
import org.stepik.core.StudyUtils.getConfigurator
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode


internal object ModuleUtils : Loggable {

    fun createStepModule(
            project: Project,
            step: StepNode,
            moduleModel: ModifiableModuleModel) {
        val lesson = step.parent
        if (lesson != null) {
            val moduleDir = arrayOf(project.basePath, lesson.path).joinToString("/")
            val stepModuleBuilder = getConfigurator(project)?.getStepModuleBuilder(moduleDir, step)
            try {
                val module = stepModuleBuilder?.createModule(moduleModel)
                if (module == null) {
                    logger.warn("Cannot create step: ${step.directory}")
                }
            } catch (e: Exception) {
                logger.warn("Cannot create step: ${step.directory}", e)
            }
        }
    }
}
