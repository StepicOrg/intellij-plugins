package org.stepik.core.testFramework.runners

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.Runner

abstract class JetRunner : Runner {
    private val logger = Logger.getInstance(JetRunner::class.java)

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

        val appConfiguration = runConfiguration.configuration
        val workingVirtualDirectory = project.baseDir.findFileByRelativePath(stepNode.path) ?: return
        setWorkingDirectory(appConfiguration, workingVirtualDirectory)
        val mainRelativePath = listOf(EduNames.SRC, language.mainFileName).joinToString("/")
        val mainVirtualFile = workingVirtualDirectory.findFileByRelativePath(mainRelativePath)
        setMainClass(application, project, appConfiguration, mainVirtualFile)
        setModule(application, project, appConfiguration, mainVirtualFile)
        setConfiguration(application, runManager, runConfiguration)
    }

    private fun getRunConfiguration(runManager: RunManagerImpl,
                                    settingName: String): RunnerAndConfigurationSettings? {
        val type = runManager.getConfigurationType(getTypeName()) ?: return null
        return runManager.getConfigurationSettingsList(type)
                .filter { x -> x.name == settingName }
                .firstOrNull()
                ?: createRunConfiguration(runManager, settingName)
    }

    protected abstract fun getTypeName(): String

    protected abstract fun getFactoryName(): String

    private fun createRunConfiguration(runManager: RunManagerImpl,
                                       settingName: String): RunnerAndConfigurationSettings? {
        val factory = runManager.getFactory(getTypeName(), getFactoryName()) ?: return null
        val runConfiguration = runManager.createRunConfiguration(settingName, factory)

        runManager.addConfiguration(runConfiguration, true)
        logger.info("Created run configuration: " + runConfiguration.name)
        return runConfiguration
    }

    protected abstract fun setWorkingDirectory(appConfiguration: RunConfiguration,
                                               workingVirtualDirectory: VirtualFile)

    protected abstract fun setMainClass(application: Application,
                                        project: Project,
                                        appConfiguration: RunConfiguration,
                                        mainVirtualFile: VirtualFile?)

    private fun setModule(application: Application,
                          project: Project,
                          appConfiguration: RunConfiguration,
                          mainVirtualFile: VirtualFile?) {
        if (mainVirtualFile != null && appConfiguration is ModuleBasedConfiguration<*>) {
            application.invokeAndWait {
                application.runWriteAction {
                    val module = ModuleUtilCore.findModuleForFile(mainVirtualFile, project)
                    appConfiguration.setModule(module)
                }
            }
        }
    }

    private fun setConfiguration(application: Application,
                                 runManager: RunManager,
                                 configuration: RunnerAndConfigurationSettings? = null) {
        application.invokeLater { runManager.selectedConfiguration = configuration }
    }
}