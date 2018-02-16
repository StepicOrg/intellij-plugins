package org.stepik.core.testFramework.processes

import com.intellij.execution.CommonJavaRunConfigurationParameters
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtil
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.createDirectories
import org.stepik.core.utils.Utils
import java.io.File
import java.io.IOException

abstract class JetProcess(project: Project, stepNode: StepNode, mainFilePath: String) : TestProcess(project, stepNode, mainFilePath) {

    open fun getMainClass(application: Application, runConfiguration: RunConfiguration, testClass: Boolean = false): String? {
        if (testClass) {
            return getTestClass()
        }
        val appConfiguration = runConfiguration as CommonJavaRunConfigurationParameters
        return application.runReadAction(Computable {
            return@Computable appConfiguration.runClass
        })
    }

    open fun getTestClass(): String? = null

    override fun start(testClass: Boolean): Process? {
        val runManager = RunManager.getInstance(project) as RunManagerImpl
        val runConfiguration = runManager.selectedConfiguration?.configuration ?: return null
        val sourcePath = getSourcePath(project, stepNode)
        val outDirectoryPath = "out/production/step${stepNode.id}"
        val baseDir = project.baseDir
        val application = ApplicationManager.getApplication()
        val outDirectory = (baseDir.findFileByRelativePath(outDirectoryPath)
                ?: createDirectories(application, baseDir, outDirectoryPath))?.path ?: return null
        val module = getModule(runConfiguration) ?: return null
        val sdk = ModuleRootManager.getInstance(module).sdk ?: return null

        application.invokeAndWait {
            Utils.saveAllDocuments(project)
        }

        val mainClass = getMainClass(application, runConfiguration, testClass) ?: return null

        val context = ProcessContext(runConfiguration, module, sdk, sourcePath, mainFilePath, mainClass, outDirectory)

        if (isNeedCompile() && !compile(context)) {
            return null
        }

        return run(context)
    }

    open fun isNeedCompile() = true

    private fun getModule(runConfiguration: RunConfiguration): Module? {
        val appConfiguration = runConfiguration as ModuleBasedConfiguration<*>
        return appConfiguration.configurationModule.module
    }

    private fun getSourcePath(project: Project, stepNode: StepNode): String =
            listOf(project.baseDir.path, stepNode.path, EduNames.SRC).joinToString(File.separator)

    open fun getCompilerPath(context: ProcessContext): File? = null

    private fun compile(context: ProcessContext): Boolean {
        try {
            clearDirectory(context.outDirectory)

            val exePath = getCompilerPath(context)
            exePath?.setExecutable(true) ?: return false
            val commandLine = GeneralCommandLine()
            commandLine.workDirectory = File(context.module.moduleFile?.parent?.path)
            commandLine.exePath = exePath.absolutePath
            if (!prepareCompileCommand(commandLine, context)) {
                return false
            }
            commandLine.createProcess().waitFor()
            return true
        } catch (e: IOException) {
            return false
        }
    }

    open fun prepareCompileCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean = false

    private fun clearDirectory(directory: String) {
        val files = File(directory).listFiles()
        if (files != null) {
            for (child in files) {
                FileUtil.delete(child)
            }
        }
    }

    abstract fun getExecutorPath(context: ProcessContext): File?

    private fun run(context: ProcessContext): Process? {
        return try {
            val exePath = getExecutorPath(context) ?: return null
            exePath.setExecutable(true)

            val commandLine = GeneralCommandLine()
            commandLine.workDirectory = File(context.outDirectory)
            commandLine.exePath = exePath.absolutePath
            if (!prepareExecuteCommand(commandLine, context)) {
                return null
            }
            commandLine.createProcess()
        } catch (e: IOException) {
            null
        }
    }

    abstract fun prepareExecuteCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean
}
