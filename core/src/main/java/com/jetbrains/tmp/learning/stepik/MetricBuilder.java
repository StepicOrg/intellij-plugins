package com.jetbrains.tmp.learning.stepik;

import com.jetbrains.tmp.learning.core.EduNames;

public class MetricBuilder {

    private String name = null;
    private String action = null;
    private String language = null;
    private Integer courseId = null;
    private Integer stepId = null;


    public static MetricBuilder getInstance() {
        return new MetricBuilder();
    }

    public MetricBuilder addTag(PluginNames name) {
        this.name = name.toString();
        return this;
    }

    public MetricBuilder addTag(MetricActions action) {
        this.action = action.toString();
        return this;
    }

    public MetricBuilder addTag(SupportedLanguages language) {
        this.language = language.toString();
        return this;
    }

    public MetricBuilder setCourseId(int courseId) {
        this.courseId = courseId;
        return this;
    }

    public MetricBuilder setStepId(int stepId) {
        this.stepId = stepId;
        return this;
    }

    public MetricsWrapper build() {
        if (check()) {
            return new MetricsWrapper(EduNames.METRIC_NAME, name, action, language, courseId, stepId);
        } else {
            return new MetricsWrapper(EduNames.INVALID, null, null, null, null, null);
        }
    }

    private boolean check() {
        if (name == null || action == null) {
            return false;
        }
        if (MetricActions.GET_COURSE.toString().equals(action) && courseId == null) {
            return false;
        }

        if (!MetricActions.GET_COURSE.toString().equals(action) && (courseId == null || stepId == null)) {
            return false;
        }
        return true;
    }

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

    public enum PluginNames {
        STEPIK_UNION("S_Union"),
        STEPIK_CLION("S_CLion"),
        STEPIK_PYCHARM("S_PyCharm");

        private final String name;

        PluginNames(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class MetricsWrapper {
        Metric metric;

        MetricsWrapper(
                String metricName,
                String tags_name,
                String tags_action,
                String tags_languages,
                Integer courseId,
                Integer stepId) {
            metric = new Metric(metricName, tags_name, tags_action, tags_languages, courseId, stepId);
        }

        static class Metric {
            final String name;
            Tags tags;
            Data data;

            Metric(
                    String metricName,
                    String name,
                    String action,
                    String language,
                    Integer courseId,
                    Integer stepId) {
                this.name = metricName;
                if (isAnyNotNull(name, action, language)) {
                    this.tags = new Tags(name, action, language);
                }
                if (isAnyNotNull(courseId, stepId)) {
                    this.data = new Data(courseId, stepId);
                }
            }

            class Tags {
                String name;
                String action;
                String language;

                Tags(String name, String action, String language) {
                    this.name = name;
                    this.action = action;
                    this.language = language;
                }
            }

            class Data {
                Integer courseId;
                Integer stepId;

                Data(Integer courseId, Integer stepId) {
                    this.courseId = courseId;
                    this.stepId = stepId;
                }
            }
        }
    }

    private static boolean isAnyNotNull(Object... objects) {
        if (objects == null) return false;
        for (Object object : objects) {
            if (object != null) return true;
        }
        return false;
    }
}

