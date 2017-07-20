package org.stepik.core.testFramework.processes

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import java.io.File
import java.io.IOException

abstract class JetProcess(project: Project, stepNode: StepNode) : TestProcess(project, stepNode) {
    private val OUTPUT_RELATIVE = "out/production"

    override fun start(): Process? {
        val runManager = RunManager.getInstance(project) as RunManagerImpl
        val language = stepNode.currentLang
        val baseDir = project.baseDir
        val mainRelativePath = listOf(stepNode.path, EduNames.SRC, language.mainFileName).joinToString("/")
        val mainVirtualFile = baseDir.findFileByRelativePath(mainRelativePath) ?: return null

        val runConfiguration = runManager.selectedConfiguration?.configuration ?: return null

        val sourcePath = getSourcePath(runConfiguration)
        val outDirectoryPath = "$OUTPUT_RELATIVE/step${stepNode.id}"
        val outDirectory = (baseDir.findFileByRelativePath(outDirectoryPath)
                ?: baseDir.createChildDirectory(null, outDirectoryPath)).path
        val module = getModule(runConfiguration) ?: return null
        val sdk = ModuleRootManager.getInstance(module).sdk
        if (sdk == null || sdk.sdkType !is JavaSdk) {
            return null
        }

        val documentManager = FileDocumentManager.getInstance()
        val editorManager = FileEditorManager.getInstance(project)

        val application = ApplicationManager.getApplication()
        application.invokeAndWait {
            editorManager.openFiles.forEach {
                val document = documentManager.getDocument(it)
                if (document != null)
                    documentManager.saveDocument(document)
            }
        }

        if (!compile(module, sdk, sourcePath, outDirectory, mainVirtualFile)) {
            return null
        }

        val mainClass = getMainClass(application, runConfiguration) ?: return null

        return run(sdk, outDirectory, mainClass)
    }

    private fun getModule(runConfiguration: RunConfiguration): Module? {
        val appConfiguration = runConfiguration as ModuleBasedConfiguration<*>
        return appConfiguration.configurationModule.module
    }

    abstract fun getSourcePath(runConfiguration: RunConfiguration): String

    abstract fun getMainClass(application: Application, runConfiguration: RunConfiguration): String?

    abstract fun getCompilerPath(sdk: Sdk): File

    private fun compile(module: Module,
                        sdk: Sdk,
                        sourcePath: String,
                        outDirectory: String,
                        mainVirtualFile: VirtualFile): Boolean {
        try {
            clearDirectory(outDirectory)

            val exePath = getCompilerPath(sdk)
            exePath.setExecutable(true)
            val commandLine = GeneralCommandLine()
            commandLine.workDirectory = File(module.moduleFile?.parent?.canonicalPath)
            commandLine.exePath = exePath.absolutePath
            if (!prepareCompileCommand(commandLine, sdk, sourcePath, outDirectory, mainVirtualFile)) {
                return false
            }
            commandLine.createProcess().waitFor()
            return true
        } catch (e: IOException) {
            return false
        }
    }

    abstract fun prepareCompileCommand(commandLine: GeneralCommandLine,
                                       sdk: Sdk,
                                       sourcePath: String,
                                       outDirectory: String,
                                       mainVirtualFile: VirtualFile): Boolean

    private fun clearDirectory(directory: String) {
        val files = File(directory).listFiles()
        if (files != null) {
            for (child in files) {
                FileUtil.delete(child)
            }
        }
    }

    abstract fun getExecutorPath(sdk: Sdk): File

    private fun run(sdk: Sdk, outDirectory: String, mainClass: String): Process? {
        try {
            val exePath = getExecutorPath(sdk)
            exePath.setExecutable(true)

            val commandLine = GeneralCommandLine()
            commandLine.workDirectory = File(outDirectory)
            commandLine.exePath = exePath.absolutePath
            if (!prepareExecuteCommand(commandLine, outDirectory, mainClass)) {
                return null
            }
            return commandLine.createProcess()
        } catch (e: IOException) {
            return null
        }
    }

    abstract fun prepareExecuteCommand(commandLine: GeneralCommandLine,
                                       outDirectory: String,
                                       mainClass: String): Boolean
}
