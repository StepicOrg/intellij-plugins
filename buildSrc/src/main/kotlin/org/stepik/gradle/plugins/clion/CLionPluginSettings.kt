package org.stepik.gradle.plugins.clion

import org.stepik.gradle.plugins.common.ProductSettings
import org.stepik.gradle.plugins.common.RepositoryType


object CLionPluginSettings : ProductSettings {

    override val extensionName = "clion"

    override val productName = "CLion"

    override val productType = "CL"

    override val productGroup = "com.jetbrains"

    override val runTaskClass = RunCLionTask::class.java

    override val repositoryType = RepositoryType.DIRECTORY

    override val repositoryTemplate = "https://download.jetbrains.com/cpp/CLion-[version].[archiveType]"

}
