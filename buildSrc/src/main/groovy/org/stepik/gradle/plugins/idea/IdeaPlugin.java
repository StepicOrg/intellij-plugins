package org.stepik.gradle.plugins.idea;

import org.jetbrains.annotations.NotNull;
import org.stepik.gradle.plugins.jetbrains.BasePlugin;
import org.stepik.gradle.plugins.jetbrains.ProductPluginExtension;

/**
 * @author meanmail
 */
public class IdeaPlugin extends BasePlugin {
    private static final String PRODUCT_NAME = "Idea";
    private static final String EXTENSION_NAME = "intellij";
    private static final String DEFAULT_REPO =
            "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij";

    public IdeaPlugin() {
        extensionName = EXTENSION_NAME;
        setProductName(PRODUCT_NAME);
        productType = "IC";
        productGroup = "com.jetbrains.intellij.idea";
        tasksGroupName = EXTENSION_NAME;
        runTaskClass = RunIdeaTask.class;
    }

    @Override
    public String getRepository(@NotNull ProductPluginExtension extension) {
        String type = extension.getType();
        final String version = extension.getVersion();
        String productName = PRODUCT_NAME.toLowerCase();

        return String.join("/",
                DEFAULT_REPO,
                productName,
                productName + type,
                version,
                productName + type + "-" + version + ".zip");
    }
}
