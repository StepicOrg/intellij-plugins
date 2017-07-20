package org.stepik.core.testFramework.processes

import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SimpleJavaSdkType
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import java.io.File

class JavaProcess(project: Project, stepNode: StepNode) : JetProcess(project, stepNode) {

    override fun getSourcePath(runConfiguration: RunConfiguration): String {
        val appConfiguration = runConfiguration as ApplicationConfiguration
        return appConfiguration.workingDirectory + File.separator + EduNames.SRC
    }

    override fun getMainClass(application: Application, runConfiguration: RunConfiguration): String? {
        val appConfiguration = runConfiguration as ApplicationConfiguration
        return application.runReadAction(Computable {
            return@Computable appConfiguration.mainClass?.name
        })
    }

    override fun getCompilerPath(sdk: Sdk): File {
        return File(SimpleJavaSdkType().getBinPath(sdk) + File.separator + "javac")
    }

    override fun prepareCompileCommand(commandLine: GeneralCommandLine,
                                       sdk: Sdk,
                                       sourcePath: String,
                                       outDirectory: String,
                                       mainVirtualFile: VirtualFile): Boolean {
        commandLine.addParameter("-sourcepath")
        commandLine.addParameter(sourcePath)
        commandLine.addParameter("-d")
        commandLine.addParameter(outDirectory)
        commandLine.addParameter(mainVirtualFile.path)
        return true
    }

    override fun getExecutorPath(sdk: Sdk): File {
        return File(SimpleJavaSdkType().getVMExecutablePath(sdk))
    }

    override fun prepareExecuteCommand(commandLine: GeneralCommandLine,
                                       outDirectory: String,
                                       mainClass: String): Boolean {
        commandLine.addParameter("-classpath")
        commandLine.addParameter(outDirectory)
        commandLine.addParameter(mainClass)
        return true
    }
}
