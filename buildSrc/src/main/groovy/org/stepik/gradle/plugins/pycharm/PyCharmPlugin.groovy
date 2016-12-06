package org.stepik.gradle.plugins.pycharm

import org.stepik.gradle.plugins.jetbrains.BasePlugin
import org.stepik.gradle.plugins.jetbrains.RepositoryType

/**
 * @author meanmail
 */
class PyCharmPlugin extends BasePlugin {
    private static final def PRODUCT_NAME = "PyCharm"
    private static final def EXTENSION_NAME = "pycharm"
    private static final def DEFAULT_REPO =
            'https://download-cf.jetbrains.com/python/pycharm-community-[version].zip'

    PyCharmPlugin() {
        extensionName = EXTENSION_NAME
        productName = PRODUCT_NAME
        productType = "CE"
        productGroup = "com.jetbrains.python"
        tasksGroupName = EXTENSION_NAME
        runTaskClass = RunPyCharmTask
        extensionInstrumentCode = false
        repositoryType = RepositoryType.DIRECTORY
    }

    @Override
    String getRepositoryTemplate() {
        return DEFAULT_REPO
    }
}