package org.stepik.gradle.plugins.clion

import org.stepik.gradle.plugins.jetbrains.BasePlugin
import org.stepik.gradle.plugins.jetbrains.RepositoryType


class CLionPlugin : BasePlugin(
        extensionName = "clion",
        productName = "CLion",
        productType = "CL",
        productGroup = "com.jetbrains",
        tasksGroupName = "clion",
        runTaskClass = RunCLionTask::class.java,
        extensionInstrumentCode = false,
        repositoryType = RepositoryType.DIRECTORY,
        repositoryTemplate = "https://download.jetbrains.com/cpp/CLion-[version].[archiveType]"
)
