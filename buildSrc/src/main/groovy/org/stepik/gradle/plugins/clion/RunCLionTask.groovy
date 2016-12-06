package org.stepik.gradle.plugins.clion

import org.stepik.gradle.plugins.jetbrains.BaseRunTask

/**
 * @author meanmail
 */
class RunCLionTask extends BaseRunTask {
    @Override
    String[] getLibs() {
        return [
                "bootstrap.jar",
                "extensions.jar",
                "util.jar",
                "trove4j.jar",
                "jdom.jar",
                "log4j.jar",
                "jna.jar"
        ]
    }

    @Override
    protected String getPlatformPrefix() {
        return "CLion"
    }

}
