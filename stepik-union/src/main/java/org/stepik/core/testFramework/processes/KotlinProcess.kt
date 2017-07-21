package org.stepik.core.testFramework.processes

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import org.jetbrains.kotlin.idea.run.JetRunConfiguration
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import java.io.File

class KotlinProcess(project: Project, stepNode: StepNode) : JetProcess(project, stepNode) {

    override fun getSourcePath(runConfiguration: RunConfiguration): String {
        val appConfiguration = runConfiguration as JetRunConfiguration
        return appConfiguration.workingDirectory + File.separator + EduNames.SRC
    }

    override fun getMainClass(application: Application, runConfiguration: RunConfiguration): String? {
        val appConfiguration = runConfiguration as JetRunConfiguration
        return application.runReadAction(Computable {
            return@Computable appConfiguration.MAIN_CLASS_NAME
        })
    }

    override fun getCompilerPath(context: ProcessContext): File {
        return File(PathManager.getHomePath() + "/plugins/Kotlin/kotlinc/bin/kotlinc")
    }


    override fun prepareCompileCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        val jdkHome = context.sdk.homePath ?: return false

        commandLine.addParameter(context.mainVirtualFile.path)
        commandLine.addParameter("-d")
        commandLine.addParameter(context.outDirectory)
        commandLine.addParameter("-jdk-home")
        commandLine.addParameter(jdkHome)
        return true
    }

    override fun getExecutorPath(context: ProcessContext): File {
        return File(PathManager.getHomePath() + "/plugins/Kotlin/kotlinc/bin/kotlin")
    }

    override fun prepareExecuteCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        commandLine.addParameter("-classpath")
        commandLine.addParameter(context.outDirectory)
        commandLine.addParameter(context.mainClass)
        return true
    }
}
