package org.stepik.core.testFramework.runners

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.processes.KotlinProcess
import org.stepik.core.testFramework.processes.TestProcess

class KotlinRunner : JetRunner() {
    override val typeName: String = "JetRunConfigurationType"

    override val factoryName: String = "Kotlin"

    override fun setWorkingDirectory(appConfiguration: RunConfiguration,
                                     workingVirtualDirectory: VirtualFile) {
        appConfiguration as CommonProgramRunConfigurationParameters
        appConfiguration.workingDirectory = workingVirtualDirectory.path
    }

    override fun setMainClass(project: Project, appConfiguration: RunConfiguration,
                              mainVirtualFile: VirtualFile?) {
        appConfiguration::class.members.find { it.name == "setRunClass" }
                ?.call(appConfiguration, "MainKt")
    }

    override fun createTestProcess(project: Project, stepNode: StepNode, mainFilePath: String): TestProcess =
            KotlinProcess(project, stepNode, mainFilePath)
}
