package org.stepik.core.testFramework.runners

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.python.run.PythonRunConfiguration

class PythonRunner : JetRunner() {
    override fun getTypeName(): String = "PythonConfigurationType"

    override fun getFactoryName(): String = "Python"

    override fun setWorkingDirectory(appConfiguration: RunConfiguration,
                                     workingVirtualDirectory: VirtualFile) {
        val workingDirectory = workingVirtualDirectory.canonicalPath
        (appConfiguration as PythonRunConfiguration).workingDirectory = workingDirectory
    }

    override fun setMainClass(application: Application,
                              project: Project,
                              appConfiguration: RunConfiguration,
                              mainVirtualFile: VirtualFile?) {
        (appConfiguration as PythonRunConfiguration).scriptName = mainVirtualFile?.name ?: ""
    }
}
