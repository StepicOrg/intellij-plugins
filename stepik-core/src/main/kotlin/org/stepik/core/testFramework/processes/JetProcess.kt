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
import org.stepik.core.EduNames
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.createDirectories
import org.stepik.core.utils.saveAllDocuments
import java.io.File
import java.io.IOException

abstract class JetProcess(project: Project, stepNode: StepNode, mainFilePath: String) :
        TestProcess(project, stepNode, mainFilePath) {

    open fun getMainClass(application: Application, runConfiguration: RunConfiguration,
                          testClass: Boolean = false): String? {
        if (testClass) {
            return this.testClass
        }

        return application.runReadAction(Computable {
            (runConfiguration as CommonJavaRunConfigurationParameters).runClass
        })
    }

    open val testClass: String? = null

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
            project.saveAllDocuments()
        }

        val mainClass = getMainClass(application, runConfiguration, testClass) ?: return null

        val context = ProcessContext(runConfiguration, module, sdk, sourcePath, mainFilePath, mainClass, outDirectory)

        if (isNeedCompile && !compile(context)) {
            return null
        }

        return run(context)
    }

    open val isNeedCompile = true

    private fun getModule(runConfiguration: RunConfiguration): Module? {
        return (runConfiguration as ModuleBasedConfiguration<*>).configurationModule.module
    }

    private fun getSourcePath(project: Project, stepNode: StepNode): String =
            listOf(project.baseDir.path, stepNode.path, EduNames.SRC).joinToString(File.separator)

    open fun getCompilerPath(context: ProcessContext): File? = null

    private fun compile(context: ProcessContext): Boolean {
        try {
            clearDirectory(context.outDirectory)

            val exePath = getCompilerPath(context) ?: return false
            exePath.setExecutable(true)

            val commandLine = GeneralCommandLine().apply {
                val path = context.module.moduleFile?.parent?.path ?: return false
                workDirectory = File(path)
                this.exePath = exePath.absolutePath
            }

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
        File(directory).listFiles()
                ?.forEach { child -> child.deleteRecursively() }
    }

    abstract fun getExecutorPath(context: ProcessContext): File?

    private fun run(context: ProcessContext): Process? {
        return try {
            val exePath = getExecutorPath(context) ?: return null
            exePath.setExecutable(true)

            val commandLine = GeneralCommandLine().apply {
                workDirectory = File(context.outDirectory)
                this.exePath = exePath.absolutePath
            }

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
