package com.jetbrains.tmp.learning.stepik.metric;

import org.jetbrains.annotations.NotNull;

public enum MetricActions {
    POST("post"),
    DOWNLOAD("download"),
    GET_COURSE("get_course");

    private final String name;

    MetricActions(String name) {
        this.name = name;
    }

    @NotNull
    public String getTag() {
        return name;
    }
}