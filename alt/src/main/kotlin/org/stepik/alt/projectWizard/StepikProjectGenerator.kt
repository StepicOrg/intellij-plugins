package org.stepik.alt.projectWizard

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectEx
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFileManager
import org.stepik.alt.courseFormat.AltTree
import org.stepik.alt.projectWizard.idea.SandboxModuleBuilder
import org.stepik.core.ProjectGenerator
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.SupportedLanguages
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.TARGET_NOT_FOUND
import org.stepik.core.projectWizard.ProjectWizardUtils
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.stepik.StepikAuthManager.currentUser
import org.stepik.core.utils.ProjectFilesUtils

object StepikProjectGenerator : ProjectGenerator, Loggable {
    var defaultLang = SupportedLanguages.JAVA8

    private fun createTreeUnderProgress(project: Project) {
        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously({
                    val indicator = ProgressManager.getInstance().progressIndicator
                    indicator.isIndeterminate = true

                    val stepikApiClient = authAndGetStepikApiClient()
                    projectRoot = AltTree(project, stepikApiClient)
                }, "Creating Project", true, project)
    }

    fun generateProject(project: Project) {
        val projectManager = getProjectManager(project)
        if (projectManager == null) {
            Metrics.createProject(project, TARGET_NOT_FOUND)
            return
        }
        projectManager.projectRoot = projectRoot
        projectManager.createdBy = currentUser.id
        projectManager.defaultLang = defaultLang

        (project as ProjectEx).setProjectName(projectRoot!!.name)

        Metrics.createProject(project)
    }


    private var projectRoot: StudyNode? = null

    fun createProject(project: Project) {
        StepikProjectGenerator.createTreeUnderProgress(project)
        StepikProjectGenerator.generateProject(project)

        val moduleModel = ApplicationManager.getApplication().runReadAction(Computable {
            ModuleManager.getInstance(project)
                    .modifiableModel
        })

        val plugins = PathManager.getPluginsPath()
        val moduleDir = FileUtil.join(plugins, "alt", "alt")

        ApplicationManager.getApplication().runWriteAction {
            SandboxModuleBuilder(moduleDir).createModule(moduleModel)

            val root = getProjectManager(project)?.projectRoot
            if (root == null) {
                logger.info("Failed to generate builders: project root is null")
                return@runWriteAction
            }

            if (root is StepNode) {
                ProjectFilesUtils.getOrCreateSrcDirectory(project, root, true, moduleModel)
            } else {
                ProjectWizardUtils.createSubDirectories(project, StepikProjectGenerator.defaultLang, root, moduleModel)
                VirtualFileManager.getInstance().syncRefresh()
            }

            moduleModel.commit()
        }
    }
}
