package org.stepik.gradle.plugins.idea

import org.stepik.gradle.plugins.jetbrains.BasePlugin
import org.stepik.gradle.plugins.jetbrains.RepositoryType


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
