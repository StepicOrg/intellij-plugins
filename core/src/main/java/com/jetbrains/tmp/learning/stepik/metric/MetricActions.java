package com.jetbrains.tmp.learning.stepik.metric;

public enum MetricActions {
    POST("post"),
    DOWNLOAD("download"),
    GET_COURSE("get_course");

    private final String name;

    MetricActions(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}