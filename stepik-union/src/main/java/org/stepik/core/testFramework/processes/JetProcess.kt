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
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.io.FileUtil
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

        val mainClass = getMainClass(application, runConfiguration) ?: return null

        val context = ProcessContext(runConfiguration, module, sdk, sourcePath, mainVirtualFile, mainClass, outDirectory)

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

    abstract fun getSourcePath(runConfiguration: RunConfiguration): String

    abstract fun getMainClass(application: Application, runConfiguration: RunConfiguration): String?

    open fun getCompilerPath(context: ProcessContext): File? = null

    private fun compile(context: ProcessContext): Boolean {
        try {
            clearDirectory(context.outDirectory)

            val exePath = getCompilerPath(context)
            exePath?.setExecutable(true) ?: return false
            val commandLine = GeneralCommandLine()
            commandLine.workDirectory = File(context.module.moduleFile?.parent?.canonicalPath)
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

    open fun prepareCompileCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        return false
    }

    private fun clearDirectory(directory: String) {
        val files = File(directory).listFiles()
        if (files != null) {
            for (child in files) {
                FileUtil.delete(child)
            }
        }
    }

    abstract fun getExecutorPath(context: ProcessContext): File

    private fun run(context: ProcessContext): Process? {
        try {
            val exePath = getExecutorPath(context)
            exePath.setExecutable(true)

            val commandLine = GeneralCommandLine()
            commandLine.workDirectory = File(context.outDirectory)
            commandLine.exePath = exePath.absolutePath
            if (!prepareExecuteCommand(commandLine, context)) {
                return null
            }
            return commandLine.createProcess()
        } catch (e: IOException) {
            return null
        }
    }

    abstract fun prepareExecuteCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean
}
