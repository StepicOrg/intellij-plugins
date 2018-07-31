package org.stepik.hyperskill.projectWizard

import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectEx
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFileManager
import org.stepik.core.ProjectGenerator
import org.stepik.core.SupportedLanguages
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.auth.StepikAuthManager.currentUser
import org.stepik.core.common.Loggable
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.getProjectManager
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.TARGET_NOT_FOUND
import org.stepik.core.projectWizard.ProjectWizardUtils.createSubDirectories
import org.stepik.core.utils.getOrCreateSrcDirectory
import org.stepik.hyperskill.courseFormat.HyperskillTree
import org.stepik.hyperskill.projectWizard.idea.SandboxModuleBuilder

object StepikProjectGenerator : ProjectGenerator, Loggable {
    var defaultLang = SupportedLanguages.JAVA8
    
    private fun createTreeUnderProgress(project: Project) {
        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously({
                    val indicator = ProgressManager.getInstance()
                            .progressIndicator
                    indicator.isIndeterminate = true
                    
                    val stepikApiClient = authAndGetStepikApiClient()
                    projectRoot = HyperskillTree(project, stepikApiClient)
                }, "Creating Project", true, project)
    }
    
    fun generateProject(project: Project) {
        val projectManager = getProjectManager(project)
        if (projectManager == null) {
            Metrics.createProject(project, TARGET_NOT_FOUND)
            return
        }
        projectManager.let {
            it.projectRoot = projectRoot
            it.createdBy = currentUser.id
            it.defaultLang = defaultLang
        }
        
        (project as ProjectEx).setProjectName(projectRoot!!.name)
        
        Metrics.createProject(project)
    }
    
    private var projectRoot: StudyNode? = null
    
    fun createProject(project: Project) {
        StepikProjectGenerator.createTreeUnderProgress(project)
        StepikProjectGenerator.generateProject(project)
        
        val moduleModel = getApplication().runReadAction(Computable {
            ModuleManager.getInstance(project)
                    .modifiableModel
        })
        
        val plugins = PathManager.getPluginsPath()
        val moduleDir = FileUtil.join(plugins, "alt", "hyperskill")
        
        getApplication().runWriteAction {
            SandboxModuleBuilder(moduleDir).createModule(moduleModel)
            
            val root = getProjectManager(project)?.projectRoot
            if (root == null) {
                logger.info("Failed to generate builders: project root is null")
                return@runWriteAction
            }
            
            if (root is StepNode) {
                getOrCreateSrcDirectory(project, root, true, moduleModel)
            } else {
                createSubDirectories(project, StepikProjectGenerator.defaultLang, root, moduleModel)
                VirtualFileManager.getInstance()
                        .syncRefresh()
            }
            
            moduleModel.commit()
        }
    }
}
