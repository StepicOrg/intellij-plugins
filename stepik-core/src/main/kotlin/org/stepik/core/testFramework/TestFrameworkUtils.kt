package org.stepik.core.testFramework

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.vfs.VirtualFile
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.utils.runWriteActionAndWait
import java.io.IOException

fun createDirectories(parent: VirtualFile, directoryRelativePath: String): VirtualFile? {
    return getApplication().runWriteActionAndWait {
        return@runWriteActionAndWait directoryRelativePath.split('/')
                .fold(parent) { dir, part ->
                    try {
                        return@fold dir.findChild(part) ?: dir.createChildDirectory(null, part)
                    } catch (e: IOException) {
                        return@runWriteActionAndWait null
                    }
                }
    }
}

class StepRunConfiguration(val stepNode: StepNode,
                           runConfiguration: RunnerAndConfigurationSettings,
                           manager: RunManagerImpl) :
        RunnerAndConfigurationSettingsImpl(manager, runConfiguration.configuration, runConfiguration.isTemplate)
