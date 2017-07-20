package org.stepik.core.testFramework.processes

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
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

    override fun getCompilerPath(sdk: Sdk): File {
        return File(PathManager.getHomePath() + "/plugins/Kotlin/kotlinc/bin/kotlinc")
    }


    override fun prepareCompileCommand(commandLine: GeneralCommandLine,
                                       sdk: Sdk,
                                       sourcePath: String,
                                       outDirectory: String,
                                       mainVirtualFile: VirtualFile): Boolean {
        val jdkHome = sdk.homePath ?: return false

        commandLine.addParameter(mainVirtualFile.path)
        commandLine.addParameter("-d")
        commandLine.addParameter(outDirectory)
        commandLine.addParameter("-jdk-home")
        commandLine.addParameter(jdkHome)
        return true
    }

    override fun getExecutorPath(sdk: Sdk): File {
        return File(PathManager.getHomePath() + "/plugins/Kotlin/kotlinc/bin/kotlin")
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
