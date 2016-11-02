package com.jetbrains.tmp.learning.stepik;

import com.jetbrains.tmp.learning.core.EduNames;

public class MetricBuilder {

    private String name = null;
    private String action = null;
    private String language = null;

    private int courseId = -1;
    private int stepId = -1;


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

    public MetricsWrapper build(){
        if (check()) {
            return new MetricsWrapper(name, action, language, courseId, stepId);
        } else {
            return new MetricsWrapper(EduNames.ILLEGAL, null, null, -1, -1);
        }
    }

    private boolean check() {
        if (name == null || action == null){
            return false;
        }
        if (MetricActions.GET_COURSE.toString().equals(action) && courseId == -1){
            return false;
        }

        if (!MetricActions.GET_COURSE.toString().equals(action) && (courseId == -1 || stepId == -1)){
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
                String tags_name,
                String tags_action,
                String tags_languages,
                int courseId,
                int stepId) {
            metric = new Metric(tags_name, tags_action, tags_languages, courseId, stepId);
        }

        static class Metric {
            final String name = "ide_plugin";
            Tags tags;
            Data data;

            Metric(
                    String name,
                    String action,
                    String language,
                    int courseId,
                    int stepId) {
                this.tags = new Tags(name, action, language);
                this.data = new Data(courseId, stepId);
            }

            class Tags {
                String name;
                String action;
                String language = "all";

                Tags(String name, String action, String language) {
                    this.name = name;
                    this.action = action;
                    if (language != null) {
                        this.language = language;
                    }
                }
            }

            class Data {
                int courseId;
                int stepId;

                Data(int courseId, int stepId) {
                    this.courseId = courseId;
                    this.stepId = stepId;
                }
            }
        }
    }
}

