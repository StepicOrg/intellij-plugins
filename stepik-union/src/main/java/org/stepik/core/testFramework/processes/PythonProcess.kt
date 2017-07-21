package org.stepik.core.testFramework.processes

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.sdk.PythonSdkType
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepNode
import java.io.File

class PythonProcess(project: Project, stepNode: StepNode) : JetProcess(project, stepNode) {

    override fun getSourcePath(runConfiguration: RunConfiguration): String {
        val appConfiguration = runConfiguration as PythonRunConfiguration
        return appConfiguration.workingDirectory + File.separator + EduNames.SRC
    }

    override fun getMainClass(application: Application, runConfiguration: RunConfiguration): String? {
        val appConfiguration = runConfiguration as PythonRunConfiguration
        return application.runReadAction(Computable {
            return@Computable appConfiguration.scriptName
        })
    }

    override fun isNeedCompile() = false

    override fun getExecutorPath(context: ProcessContext): File {
        val sdkHome = (context.runConfiguration as PythonRunConfiguration).sdkHome ?: ""
        val executable = PythonSdkType.getPythonExecutable(sdkHome)
        return File(executable)
    }

    override fun prepareExecuteCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        commandLine.addParameter(context.mainVirtualFile.path)
        return true
    }
}
