package com.jetbrains.tmp.learning.stepik.metric;

import com.jetbrains.tmp.learning.core.EduNames;

import static com.jetbrains.tmp.learning.stepik.metric.MetricUtils.isAllNull;

public class MetricsWrapper {
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

    MetricsWrapper(
            String metricName) {
        metric = new Metric(metricName, null, null, null, null, null);
    }

    static class Metric {
        final String name;
        Tags tags;
        Data data;

        private Metric(
                String metricName,
                String name,
                String action,
                String language,
                Integer courseId,
                Integer stepId) {
            this.name = metricName;
            if (!isAllNull(name, action, language)) {
                this.tags = new Tags(name, action, language);
            }
            if (!isAllNull(courseId, stepId)) {
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

    public boolean isCorrect() {
        return !EduNames.INVALID.equals(metric.name);
    }
}