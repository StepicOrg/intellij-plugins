package org.stepik.core.testFramework.processes

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.run.PythonRunConfigurationParams
import com.jetbrains.python.sdk.PythonSdkType.getPythonExecutable
import org.stepik.core.courseFormat.StepNode
import java.io.File

class PythonProcess(project: Project, stepNode: StepNode, mainFilePath: String) : JetProcess(project, stepNode, mainFilePath) {

    override fun getMainClass(runConfiguration: RunConfiguration, testClass: Boolean): String? {
        if (testClass) {
            return null
        }

        return getApplication().runReadAction(Computable {
            (runConfiguration as PythonRunConfigurationParams).scriptName
        })
    }

    override val isNeedCompile = false

    override fun getExecutorPath(context: ProcessContext): File? {
        val sdkHome = (context.runConfiguration as PythonRunConfiguration).sdkHome ?: ""
        val executable = getPythonExecutable(sdkHome) ?: return null
        return File(executable)
    }

    override fun prepareExecuteCommand(commandLine: GeneralCommandLine, context: ProcessContext): Boolean {
        commandLine.addParameter(context.mainFilePath)
        return true
    }
}
