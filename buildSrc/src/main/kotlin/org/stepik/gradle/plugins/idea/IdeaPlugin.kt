package org.stepik.gradle.plugins.idea

import org.stepik.gradle.plugins.common.BasePlugin
import org.stepik.gradle.plugins.common.RepositoryType


class IdeaPlugin : BasePlugin(
        extensionName = "intellij",
        productName = "Idea",
        productType = "IC",
        productGroup = "com.jetbrains.intellij.idea",
        tasksGroupName = "intellij",
        runTaskClass = RunIdeaTask::class.java,
        extensionInstrumentCode = true,
        repositoryType = RepositoryType.MAVEN,
        repositoryTemplate = "https://www.jetbrains.com/intellij-repository/releases"
)
