package com.jetbrains.tmp.learning.stepik.metric;

import com.jetbrains.tmp.learning.core.EduNames;

import static com.jetbrains.tmp.learning.stepik.metric.MetricUtils.isAllNull;

public class MetricsWrapper {
    private static MetricsWrapper InvalidMetricWrapper;

    private Metric metric;

    private MetricsWrapper() {
        metric = new Metric();
    }

    MetricsWrapper(MetricBuilder metricBuilder) {
        metric = new Metric(metricBuilder);
    }

    static MetricsWrapper getInvalidWrapper() {
        if (InvalidMetricWrapper == null) {
            InvalidMetricWrapper = new MetricsWrapper();
        }
        return InvalidMetricWrapper;
    }

    private static class Metric {
        final String name;
        Tags tags;
        Data data;

        private Metric(MetricBuilder mb) {
            this.name = mb.name;
            if (!isAllNull(name, mb.action, mb.language)) {
                this.tags = new Tags(name, mb.action, mb.language);
            }
            if (!isAllNull(mb.courseId, mb.stepId)) {
                this.data = new Data(mb.courseId, mb.stepId);
            }
        }

        private Metric() {
            this.name = EduNames.INVALID;
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