package org.stepik.gradle.plugins.idea

import org.stepik.gradle.plugins.common.ProductSettings
import org.stepik.gradle.plugins.common.RepositoryType


object IdeaPluginSettings : ProductSettings {

    override val extensionName = "intellij"

    override val productName = "Idea"

    override val productType = "IC"

    override val productGroup = "com.jetbrains.intellij.idea"

    override val runTaskClass = RunIdeaTask::class.java

    override val repositoryType = RepositoryType.MAVEN

    override val repositoryTemplate = "https://www.jetbrains.com/intellij-repository/releases"

}
