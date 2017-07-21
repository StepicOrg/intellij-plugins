package org.stepik.core.testFramework.processes

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.vfs.VirtualFile

data class ProcessContext(val runConfiguration: RunConfiguration,
                          val module: Module,
                          val sdk: Sdk,
                          val sourcePath: String,
                          val mainVirtualFile: VirtualFile,
                          val mainClass: String,
                          val outDirectory: String)

