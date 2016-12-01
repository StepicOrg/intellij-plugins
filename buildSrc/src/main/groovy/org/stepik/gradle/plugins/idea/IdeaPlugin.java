package org.stepik.gradle.plugins.idea;

import org.stepik.gradle.plugins.jetbrains.BasePlugin;

/**
 * @author meanmail
 */
public class IdeaPlugin extends BasePlugin {
    private static final String PRODUCT_NAME = "Idea";
    private static final String EXTENSION_NAME = "intellij";
    private static final String DEFAULT_REPO =
            "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/${productName}" +
                    "/${productName}${productType}/${version}/${productName}${productType}-${version}.zip";

    public IdeaPlugin() {
        extensionName = EXTENSION_NAME;
        setProductName(PRODUCT_NAME);
        productType = "IC";
        productGroup = "com.jetbrains.intellij.idea";
        tasksGroupName = EXTENSION_NAME;
        runTaskClass = RunIdeaTask.class;
    }

    @Override
    public String getRepositoryTemplate() {
        return DEFAULT_REPO;
    }
}
