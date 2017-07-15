package org.stepik.core.testFramework.runners

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.TestRunner

class JavaTestRunner : TestRunner {

    override fun updateRunConfiguration(project: Project, stepNode: StepNode) {
        val application = ApplicationManager.getApplication()
        val runManager = RunManager.getInstance(project) as RunManagerImpl
        val language = stepNode.currentLang

        val settingName = "${stepNode.parent?.name ?: "Lesson"} | step ${stepNode.position} ($language)"

        val type = runManager.getConfigurationType(typeName)
        if (type == null) {
            setConfiguration(application, runManager)
            return
        }

        val runConfiguration: RunnerAndConfigurationSettings? = runManager.getConfigurationSettingsList(type)
                .filter { x -> x.name == settingName }
                .firstOrNull()
                ?: createRunConfiguration(runManager, settingName)

        if (runConfiguration == null) {
            setConfiguration(application, runManager)
            return
        }

        val appConfiguration = (runConfiguration.configuration as ApplicationConfiguration)

        val workingVirtualDirectory = project.baseDir.findFileByRelativePath(stepNode.path) ?: return
        val workingDirectory = workingVirtualDirectory.canonicalPath
        appConfiguration.workingDirectory = workingDirectory
        val mainRelativePath = listOf(EduNames.SRC, language.mainFileName).joinToString("/")
        val mainVirtualFile = workingVirtualDirectory.findFileByRelativePath(mainRelativePath)
        val psiManager = PsiManager.getInstance(project)
        if (mainVirtualFile != null) {
            val mainPsiFile = Array<PsiFile?>(1, { null })
            application.invokeAndWait {
                mainPsiFile[0] = psiManager.findFile(mainVirtualFile)
            }
            val mainPsiClass = mainPsiFile[0]
            if (mainPsiClass is PsiJavaFile) {
                application.invokeAndWait {
                    val mainClass = mainPsiClass.classes
                            .filter { it.findMethodsByName("main", false).isNotEmpty() }
                            .getOrNull(0)
                            ?: return@invokeAndWait
                    appConfiguration.mainClass = mainClass
                }
            }
        }

        if (workingDirectory != null) {
            val module = Array<Module?>(1, { null })
            application.invokeAndWait {
                application.runWriteAction {
                    module[0] = ModuleManager.getInstance(project).findModuleByName("step${stepNode.id}")
                    appConfiguration.setModule(module[0])
                }
            }
        }

        setConfiguration(application, runManager, runConfiguration)
    }

    private fun createRunConfiguration(runManager: RunManagerImpl, settingName: String): RunnerAndConfigurationSettings? {
        val factory = runManager.getFactory(typeName, factoryName) ?: return null
        val runConfiguration = runManager.createRunConfiguration(settingName, factory)

        runManager.addConfiguration(runConfiguration, true)
        logger.info("Created run configuration: " + runConfiguration.name)
        return runConfiguration
    }

    companion object {
        private val logger = Logger.getInstance(JavaTestRunner::class.java)
        private val typeName = "Application"
        private val factoryName = "Application"

        fun setConfiguration(application: Application, runManager: RunManager, configuration: RunnerAndConfigurationSettings? = null) {
            application.invokeLater { runManager.selectedConfiguration = configuration }
        }
    }
}
