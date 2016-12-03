package org.stepik.gradle.plugins.idea

import org.stepik.gradle.plugins.jetbrains.BaseRunTask

/**
 * @author meanmail
 */
class RunIdeaTask extends BaseRunTask {
    @Override
    protected String[] getLibs() {
        return [
                "idea_rt.jar",
                "idea.jar",
                "bootstrap.jar",
                "extensions.jar",
                "util.jar",
                "openapi.jar",
                "trove4j.jar",
                "jdom.jar",
                "log4j.jar"
        ]
    }

    @Override
    protected String getPlatformPrefix() {
        return "Idea"
    }

}
