package org.stepik.core.testFramework.processes

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.SimpleJavaSdkType
import org.stepik.core.courseFormat.StepNode
import java.io.File

class JavaProcess(project: Project, stepNode: StepNode, mainFilePath: String) : JetProcess(project, stepNode, mainFilePath) {

    override fun getCompilerPath(context: ProcessContext): File =
            File(SimpleJavaSdkType().getBinPath(context.sdk) + File.separator + "javac")

    override fun prepareCompileCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        commandLine.addParameter("-sourcepath")
        commandLine.addParameter(context.sourcePath)
        commandLine.addParameter("-d")
        commandLine.addParameter(context.outDirectory)
        commandLine.addParameter(context.mainFilePath)
        return true
    }

    override fun getExecutorPath(context: ProcessContext): File? {
        val executable = SimpleJavaSdkType().getVMExecutablePath(context.sdk) ?: return null
        return File(executable)
    }

    override fun prepareExecuteCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        commandLine.addParameter("-classpath")
        commandLine.addParameter(context.outDirectory)
        commandLine.addParameter(context.mainClass)
        return true
    }
}
