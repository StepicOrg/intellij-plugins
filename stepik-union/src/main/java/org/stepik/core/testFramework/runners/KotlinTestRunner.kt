package org.stepik.core.testFramework.runners

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.run.JetRunConfiguration
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.TestRunner

object KotlinTestRunner : TestRunner {
    private val logger = Logger.getInstance(JavaTestRunner::class.java)
    private val typeName = "JetRunConfigurationType"
    private val factoryName = "Kotlin"

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

        val appConfiguration = (runConfiguration.configuration as JetRunConfiguration)

        val workingVirtualDirectory = project.baseDir.findFileByRelativePath(stepNode.path) ?: return
        setWorkingDirectory(appConfiguration, workingVirtualDirectory)
        val mainRelativePath = listOf(EduNames.SRC, language.mainFileName).joinToString("/")
        val mainVirtualFile = workingVirtualDirectory.findFileByRelativePath(mainRelativePath)

        setModule(application, project, appConfiguration, mainVirtualFile)
        appConfiguration.MAIN_CLASS_NAME = "MainKt"
        setConfiguration(application, runManager, runConfiguration)
    }

    private fun getRunConfiguration(runManager: RunManagerImpl,
                                    settingName: String): RunnerAndConfigurationSettings? {
        val type = runManager.getConfigurationType(typeName) ?: return null
        return runManager.getConfigurationSettingsList(type)
                .filter { x -> x.name == settingName }
                .firstOrNull()
                ?: createRunConfiguration(runManager, settingName)
    }

    fun createRunConfiguration(runManager: RunManagerImpl,
                               settingName: String): RunnerAndConfigurationSettings? {
        val factory = runManager.getFactory(typeName, factoryName) ?: return null
        val runConfiguration = runManager.createRunConfiguration(settingName, factory)

        runManager.addConfiguration(runConfiguration, true)
        logger.info("Created run configuration: " + runConfiguration.name)
        return runConfiguration
    }

    private fun setWorkingDirectory(appConfiguration: JetRunConfiguration,
                                    workingVirtualDirectory: VirtualFile) {
        val workingDirectory = workingVirtualDirectory.canonicalPath
        appConfiguration.workingDirectory = workingDirectory
    }

    private fun setModule(application: Application,
                          project: Project,
                          appConfiguration: JetRunConfiguration,
                          mainVirtualFile: VirtualFile?) {
        if (mainVirtualFile != null) {
            application.invokeAndWait {
                application.runWriteAction {
                    val module = ModuleUtilCore.findModuleForFile(mainVirtualFile, project)
                    appConfiguration.setModule(module)
                }
            }
        }
    }

    fun setConfiguration(application: Application,
                         runManager: RunManager,
                         configuration: RunnerAndConfigurationSettings? = null) {
        application.invokeLater { runManager.selectedConfiguration = configuration }
    }
}
