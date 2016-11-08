package com.jetbrains.tmp.learning.stepik.metric;

import org.jetbrains.annotations.NotNull;

public enum PluginNames {
    STEPIK_UNION("S_Union"),
    STEPIK_CLION("S_CLion"),
    STEPIK_PYCHARM("S_PyCharm");

    private final String name;

    PluginNames(String name) {
        this.name = name;
    }

    @NotNull
    public String getTag() {
        return name;
    }
}