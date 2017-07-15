package org.stepik.core.testFramework.runners

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiClassUtil
import com.intellij.psi.util.PsiMethodUtil
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.TestRunner

object JavaTestRunner : TestRunner {
    private val logger = Logger.getInstance(JavaTestRunner::class.java)
    private val typeName = "Application"
    private val factoryName = "Application"

    override fun updateRunConfiguration(project: Project, stepNode: StepNode) {
        val application = ApplicationManager.getApplication()
        val runManager = RunManager.getInstance(project) as RunManagerImpl
        val language = stepNode.currentLang

        val settingName = "${stepNode.parent?.name ?: "Lesson"} | step ${stepNode.position} ($language)"

        val runConfiguration = getRunConfiguration(runManager, settingName)
        if (runConfiguration == null) {
            setConfiguration(application, runManager)
            return
        }

        val appConfiguration = (runConfiguration.configuration as ApplicationConfiguration)

        val workingVirtualDirectory = project.baseDir.findFileByRelativePath(stepNode.path) ?: return
        setWorkingDirectory(appConfiguration, workingVirtualDirectory)
        val mainRelativePath = listOf(EduNames.SRC, language.mainFileName).joinToString("/")
        val mainVirtualFile = workingVirtualDirectory.findFileByRelativePath(mainRelativePath)

        setMainClass(application, project, appConfiguration, mainVirtualFile)
        setModule(application, project, appConfiguration, mainVirtualFile)
        setConfiguration(application, runManager, runConfiguration)
    }

    private fun getRunConfiguration(runManager: RunManagerImpl, settingName: String): RunnerAndConfigurationSettings? {
        val type = runManager.getConfigurationType(typeName)?: return null
        return runManager.getConfigurationSettingsList(type)
                    .filter { x -> x.name == settingName }
                    .firstOrNull()
                    ?: createRunConfiguration(runManager, settingName)
    }

    fun createRunConfiguration(runManager: RunManagerImpl, settingName: String): RunnerAndConfigurationSettings? {
        val factory = runManager.getFactory(typeName, factoryName) ?: return null
        val runConfiguration = runManager.createRunConfiguration(settingName, factory)

        runManager.addConfiguration(runConfiguration, true)
        logger.info("Created run configuration: " + runConfiguration.name)
        return runConfiguration
    }

    private fun setWorkingDirectory(appConfiguration: ApplicationConfiguration, workingVirtualDirectory: VirtualFile) {
        val workingDirectory = workingVirtualDirectory.canonicalPath
        appConfiguration.workingDirectory = workingDirectory
    }

    fun setMainClass(application: Application, project: Project, appConfiguration: ApplicationConfiguration, mainVirtualFile: VirtualFile?): VirtualFile? {
        if (mainVirtualFile != null) {
            val mainPsiFile = Array<PsiFile?>(1, { null })
            application.invokeAndWait {
                val psiManager = PsiManager.getInstance(project)
                mainPsiFile[0] = psiManager.findFile(mainVirtualFile)
            }
            val mainPsiClass = mainPsiFile[0]
            if (mainPsiClass is PsiJavaFile) {
                DumbService.getInstance(project).runReadActionInSmartMode {
                    val mainClass = mainPsiClass.classes
                            .filter {
                                PsiClassUtil.isRunnableClass(it, false) && PsiMethodUtil.hasMainMethod(it)
                            }
                            .getOrNull(0)
                            ?: return@runReadActionInSmartMode
                    appConfiguration.mainClass = mainClass
                }
            }
        }
        return mainVirtualFile
    }

    private fun setModule(application: Application, project: Project, appConfiguration: ApplicationConfiguration, mainVirtualFile: VirtualFile?) {
        if (mainVirtualFile != null) {
            application.invokeAndWait {
                application.runWriteAction {
                    val module = ModuleUtilCore.findModuleForFile(mainVirtualFile, project)
                    appConfiguration.setModule(module)
                }
            }
        }
    }

    fun setConfiguration(application: Application, runManager: RunManager, configuration: RunnerAndConfigurationSettings? = null) {
        application.invokeLater { runManager.selectedConfiguration = configuration }
    }
}
