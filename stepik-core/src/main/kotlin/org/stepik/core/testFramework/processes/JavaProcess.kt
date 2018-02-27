package org.stepik.core.testFramework.processes

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.SimpleJavaSdkType
import org.stepik.core.courseFormat.StepNode
import java.io.File

class JavaProcess(project: Project, stepNode: StepNode, mainFilePath: String) : JetProcess(project, stepNode, mainFilePath) {

    override fun getCompilerPath(context: ProcessContext) =
            File(SimpleJavaSdkType().getBinPath(context.sdk) + File.separator + "javac")

    override fun prepareCompileCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        commandLine.run {
            addParameter("-sourcepath")
            addParameter(context.sourcePath)
            addParameter("-d")
            addParameter(context.outDirectory)
            addParameter(context.mainFilePath)
        }
        return true
    }

    override fun getExecutorPath(context: ProcessContext): File? {
        val executable = SimpleJavaSdkType().getVMExecutablePath(context.sdk) ?: return null
        return File(executable)
    }

    override fun prepareExecuteCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        commandLine.run {
            addParameter("-classpath")
            addParameter(context.outDirectory)
            addParameter(context.mainClass)
        }
        return true
    }
}
