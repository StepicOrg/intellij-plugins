package org.stepik.gradle.plugins.pycharm;

import org.jetbrains.annotations.NotNull;
import org.stepik.gradle.plugins.jetbrains.BasePlugin;
import org.stepik.gradle.plugins.jetbrains.ProductPluginExtension;

/**
 * @author meanmail
 */
public class PyCharmPlugin extends BasePlugin {
    private static final String PRODUCT_NAME = "PyCharm";
    private static final String EXTENSION_NAME = "pycharm";
    private static final String DEFAULT_REPO = "https://download-cf.jetbrains.com/python/pycharm-community-";

    public PyCharmPlugin() {
        extensionName = EXTENSION_NAME;
        setProductName(PRODUCT_NAME);
        productType = "CE";
        productGroup = "org.jetbrains.python";
        tasksGroupName = EXTENSION_NAME;
        runTaskClass = RunPyCharmTask.class;
    }

    @Override
    public String getRepository(@NotNull final ProductPluginExtension extension) {
        return DEFAULT_REPO + extension.getVersion() + ".zip";
    }
}
