package org.stepik.gradle.plugins.idea;

import org.stepik.gradle.plugins.jetbrains.BaseRunTask;

public class RunIdeaTask extends BaseRunTask {
    @Override
    public String[] getLibs() {
        return new String[] {
                "idea_rt.jar",
                "idea.jar",
                "bootstrap.jar",
                "extensions.jar",
                "util.jar",
                "openapi.jar",
                "trove4j.jar",
                "jdom.jar",
                "log4j.jar"
        };
    }

    @Override
    public String getPlatformPrefix() {
        return "Idea";
    }

}
