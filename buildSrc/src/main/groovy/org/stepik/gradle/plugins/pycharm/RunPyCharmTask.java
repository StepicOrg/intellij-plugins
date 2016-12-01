package org.stepik.gradle.plugins.pycharm;

import org.stepik.gradle.plugins.jetbrains.BaseRunTask;

/**
 * @author meanmail
 */
public class RunPyCharmTask extends BaseRunTask {
    @Override
    public String[] getLibs() {
        return new String[]{
                "bootstrap.jar",
                "extensions.jar",
                "util.jar",
                "trove4j.jar",
                "jdom.jar",
                "log4j.jar",
                "jna.jar"
        };
    }

    @Override
    public String getPlatformPrefix() {
        return "PyCharmCore";
    }

}
