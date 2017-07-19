package org.stepik.core.testFramework.processes

import com.intellij.execution.RunManager
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SimpleJavaSdkType
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import java.io.File

class JavaProcess(project: Project, stepNode: StepNode) : TestProcess(project, stepNode) {
    private val OUTPUT_RELATIVE = "out/production"

    override fun start(): Process? {
        val runManager = RunManager.getInstance(project) as RunManagerImpl
        val language = stepNode.currentLang
        val baseDir = project.baseDir
        val mainRelativePath = listOf(stepNode.path, EduNames.SRC, language.mainFileName).joinToString("/")
        val mainVirtualFile = baseDir.findFileByRelativePath(mainRelativePath) ?: return null

        val configuration = runManager.selectedConfiguration?.configuration ?: return null
        val appConfiguration = configuration as ApplicationConfiguration

        val sourcePath = appConfiguration.workingDirectory + File.separator + EduNames.SRC
        val outDirectoryPath = "$OUTPUT_RELATIVE/step${stepNode.id}"
        val outDirectory = (baseDir.findFileByRelativePath(outDirectoryPath)
                ?: baseDir.createChildDirectory(null, outDirectoryPath)).path
        val module = appConfiguration.configurationModule.module ?: return null
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

        compile(module, sdk, sourcePath, outDirectory, mainVirtualFile)

        val mainClass = application.runReadAction(Computable {
            return@Computable appConfiguration.mainClass?.name
        }) ?: return null

        return run(sdk, outDirectory, mainClass)
    }

    private fun run(sdk: Sdk, outDirectory: String, mainClass: String): Process {
        val commandLine = GeneralCommandLine()
        commandLine.workDirectory = File(outDirectory)
        commandLine.exePath = SimpleJavaSdkType().getVMExecutablePath(sdk)
        commandLine.addParameter("-classpath")
        commandLine.addParameter(outDirectory)
        commandLine.addParameter(mainClass)
        return commandLine.createProcess()
    }

    private fun compile(module: Module, sdk: Sdk, sourcePath: String, outDirectory: String, mainVirtualFile: VirtualFile) {
        clearDirectory(outDirectory)

        val commandLine = GeneralCommandLine()
        commandLine.workDirectory = File(module.moduleFile?.parent?.canonicalPath)
        commandLine.exePath = SimpleJavaSdkType().getBinPath(sdk) + File.separator + "javac"
        commandLine.addParameter("-sourcepath")
        commandLine.addParameter(sourcePath)
        commandLine.addParameter("-d")
        commandLine.addParameter(outDirectory)
        commandLine.addParameter(mainVirtualFile.path)
        commandLine.createProcess().waitFor()
    }

    private fun clearDirectory(directory: String) {
        val files = File(directory).listFiles()
        if (files != null) {
            for (child in files) {
                FileUtil.delete(child)
            }
        }
    }
}
