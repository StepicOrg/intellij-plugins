package org.stepik.gradle.plugins.idea

import org.stepik.gradle.plugins.jetbrains.BasePlugin

/**
 * @author meanmail
 */
class IdeaPlugin extends BasePlugin {
    private static final def PRODUCT_NAME = "Idea"
    private static final def EXTENSION_NAME = "intellij"
    private static final def DEFAULT_REPO =
            'https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/${productName}' +
                    '/${productName}${productType}/${version}/${productName}${productType}-${version}.zip'

    IdeaPlugin() {
        extensionName = EXTENSION_NAME
        productName = PRODUCT_NAME
        productType = "IC"
        productGroup = "com.jetbrains.intellij.idea"
        tasksGroupName = EXTENSION_NAME
        runTaskClass = RunIdeaTask
    }

    @Override
    String getRepositoryTemplate() {
        return DEFAULT_REPO
    }
}
