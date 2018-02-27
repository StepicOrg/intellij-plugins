package org.stepik.core.testFramework.processes

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode
import java.io.File

class KotlinProcess(project: Project, stepNode: StepNode, mainFilePath: String) : JetProcess(project, stepNode, mainFilePath) {

    override fun getCompilerPath(context: ProcessContext): File {
        val relativeCompilerPath = listOf("plugins", "Kotlin", "kotlinc", "bin", "kotlinc").joinToString(File.separator)
        return File(PathManager.getHomePath(), relativeCompilerPath)
    }

    override fun prepareCompileCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        val jdkHome = context.sdk.homePath ?: return false

        commandLine.run {
            addParameter(context.mainFilePath)
            addParameter("-d")
            addParameter(context.outDirectory)
            addParameter("-jdk-home")
            addParameter(jdkHome)
        }
        return true
    }

    override fun getExecutorPath(context: ProcessContext): File? {
        val relativeRunnerPath = listOf("plugins", "Kotlin", "kotlinc", "bin", "kotlin").joinToString(File.separator)
        return File(PathManager.getHomePath(), relativeRunnerPath)
    }

    override fun prepareExecuteCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        commandLine.run {
            addParameter("-classpath")
            addParameter(context.outDirectory)
            addParameter(context.mainClass)
        }
        return true
    }

    override val testClass = "TestKt"
}
