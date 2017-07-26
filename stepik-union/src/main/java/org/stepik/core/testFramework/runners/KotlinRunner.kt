package org.stepik.core.testFramework.runners

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.run.JetRunConfiguration
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.processes.KotlinProcess
import org.stepik.core.testFramework.processes.TestProcess

class KotlinRunner : JetRunner() {
    override fun getTypeName(): String = "JetRunConfigurationType"

    override fun getFactoryName(): String = "Kotlin"

    override fun setWorkingDirectory(appConfiguration: RunConfiguration,
                                     workingVirtualDirectory: VirtualFile) {
        val workingDirectory = workingVirtualDirectory.canonicalPath
        (appConfiguration as JetRunConfiguration).workingDirectory = workingDirectory
    }

    override fun setMainClass(application: Application,
                              project: Project,
                              appConfiguration: RunConfiguration,
                              mainVirtualFile: VirtualFile?) {
        (appConfiguration as JetRunConfiguration).MAIN_CLASS_NAME = "MainKt"
    }

    override fun createTestProcess(project: Project, stepNode: StepNode, mainFilePath: String): TestProcess {
        return KotlinProcess(project, stepNode, mainFilePath)
    }
}
