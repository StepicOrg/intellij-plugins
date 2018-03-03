package org.stepik.gradle.plugins.pycharm

import org.stepik.gradle.plugins.common.BasePlugin
import org.stepik.gradle.plugins.common.RepositoryType


class PyCharmPlugin : BasePlugin(
        extensionName = "pycharm",
        productName = "PyCharm",
        productType = "CE",
        productGroup = "com.jetbrains",
        tasksGroupName = "pycharm",
        runTaskClass = RunPyCharmTask::class.java,
        extensionInstrumentCode = false,
        repositoryType = RepositoryType.DIRECTORY,
        repositoryTemplate = "https://download-cf.jetbrains.com/" +
                "python/pycharm-community-[version].[archiveType]"
)
