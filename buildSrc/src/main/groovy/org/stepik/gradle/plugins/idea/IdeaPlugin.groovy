package org.stepik.gradle.plugins.idea

import org.stepik.gradle.plugins.jetbrains.BasePlugin
import org.stepik.gradle.plugins.jetbrains.RepositoryType

/**
 * @author meanmail
 */
class IdeaPlugin extends BasePlugin {
    private static final def PRODUCT_NAME = "Idea"
    private static final def EXTENSION_NAME = "intellij"
    private static final def DEFAULT_REPO = 'https://www.jetbrains.com/intellij-repository/releases'

    IdeaPlugin() {
        extensionName = EXTENSION_NAME
        productName = PRODUCT_NAME
        productType = "IC"
        productGroup = "com.jetbrains.intellij.idea"
        tasksGroupName = EXTENSION_NAME
        runTaskClass = RunIdeaTask
        extensionInstrumentCode = true
        repositoryType = RepositoryType.MAVEN
    }

    @Override
    String getRepositoryTemplate() {
        return DEFAULT_REPO
    }
}
