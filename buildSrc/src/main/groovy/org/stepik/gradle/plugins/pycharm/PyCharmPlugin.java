package org.stepik.gradle.plugins.pycharm;

import org.stepik.gradle.plugins.jetbrains.BasePlugin;

/**
 * @author meanmail
 */
public class PyCharmPlugin extends BasePlugin {
    private static final String PRODUCT_NAME = "PyCharm";
    private static final String EXTENSION_NAME = "pycharm";
    private static final String DEFAULT_REPO = "https://download-cf.jetbrains.com/python/pycharm-community-${version}.zip";

    public PyCharmPlugin() {
        extensionName = EXTENSION_NAME;
        setProductName(PRODUCT_NAME);
        productType = "CE";
        productGroup = "org.jetbrains.python";
        tasksGroupName = EXTENSION_NAME;
        runTaskClass = RunPyCharmTask.class;
    }

    @Override
    public String getRepositoryTemplate() {
        return DEFAULT_REPO;
    }
}
