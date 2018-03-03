package org.stepik.gradle.plugins.pycharm

import org.stepik.gradle.plugins.common.ProductSettings
import org.stepik.gradle.plugins.common.RepositoryType


object PyCharmPluginSettings : ProductSettings {

    override val extensionName = "pycharm"

    override val productName = "PyCharm"

    override val productType = "CE"

    override val productGroup = "com.jetbrains"

    override val runTaskClass = RunPyCharmTask::class.java

    override val repositoryType = RepositoryType.DIRECTORY

    override val repositoryTemplate = "https://download-cf.jetbrains.com/" +
            "python/pycharm-community-[version].[archiveType]"

}
