package org.stepik.core.testFramework.runners

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.python.packaging.PyPackageManager
import com.jetbrains.python.psi.LanguageLevel
import com.jetbrains.python.psi.LanguageLevel.fromPythonVersion
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.sdk.PyDetectedSdk
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.flavors.PythonSdkFlavor
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.processes.PythonProcess
import org.stepik.core.utils.getRelativePath
import java.io.File

class PythonRunner : JetRunner() {
    override val typeName: String = "PythonConfigurationType"

    override val factoryName: String = "Python"

    override fun setWorkingDirectory(appConfiguration: RunConfiguration,
                                     workingVirtualDirectory: VirtualFile) {
        appConfiguration as PythonRunConfiguration
        appConfiguration.workingDirectory = workingVirtualDirectory.path
    }

    override fun setMainClass(project: Project, appConfiguration: RunConfiguration,
                              mainVirtualFile: VirtualFile?) {
        appConfiguration as PythonRunConfiguration

        val workingDirectory = appConfiguration.workingDirectory
        val scriptPath = mainVirtualFile?.path
        val scriptRelativePath = if (workingDirectory != null && scriptPath != null) {
            workingDirectory.getRelativePath(scriptPath)
        } else {
            ""
        }

        appConfiguration.scriptName = scriptRelativePath
    }

    override fun setSdk(project: Project,
                        appConfiguration: RunConfiguration,
                        mainVirtualFile: VirtualFile?) {
        mainVirtualFile ?: return
        val virtualEnvPath = listOf(project.basePath, ".idea", "virtualenvs").joinToString(File.separator)
        val sdkPath = listOf(virtualEnvPath, "bin", "python").joinToString(File.separator)
        val packageManager = PyPackageManager.getInstance(PyDetectedSdk(getBaseSdk()))
        val path = packageManager.createVirtualEnv(virtualEnvPath, false)
        getApplication().invokeAndWait {
            val sdks = ProjectJdkTable.getInstance().getSdksOfType(PythonSdkType.getInstance())
            if (sdks.count { it.homePath == sdkPath } == 0) {
                val sdkType = PythonSdkType.getInstance()
                SdkConfigurationUtil.createAndAddSDK(FileUtil.toSystemDependentName(path), sdkType)
            }
        }
        (appConfiguration as PythonRunConfiguration).run {
            isUseModuleSdk = false
            sdkHome = path
        }
    }

    private fun getBaseSdk(): String {
        val baseLevel = LanguageLevel.PYTHON31
        val flavor = PythonSdkFlavor.getApplicableFlavors(false)[0]
        val baseSdks = flavor.suggestHomePaths()
        val baseSdk = baseSdks.firstOrNull {
            val versionString = flavor.getVersionString(it)
            val prefix = "${flavor.name} "
            if (versionString?.startsWith(prefix) == true) {
                val version = versionString.substring(prefix.length)
                val level = fromPythonVersion(version)
                if (level.isAtLeast(baseLevel)) {
                    return@firstOrNull true
                }
            }
            return@firstOrNull false
        }

        return baseSdk ?: baseSdks.firstOrNull() ?: ""
    }

    override fun createTestProcess(project: Project, stepNode: StepNode, mainFilePath: String) =
            PythonProcess(project, stepNode, mainFilePath)
}
