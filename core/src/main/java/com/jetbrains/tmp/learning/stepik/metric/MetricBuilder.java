package com.jetbrains.tmp.learning.stepik.metric;

import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.SupportedLanguages;

import static com.jetbrains.tmp.learning.stepik.metric.MetricUtils.isAllNull;
import static com.jetbrains.tmp.learning.stepik.metric.MetricUtils.isAnyNull;

public class MetricBuilder {
    private String name;
    private String action;
    private String language;
    private Integer courseId;
    private Integer stepId;

    public static MetricsWrapper INVALID_METRICS_WRAPPER = new MetricsWrapper(EduNames.INVALID);

    public MetricBuilder() {}

    public MetricBuilder addTag(PluginNames name) {
        this.name = name.getTag();
        return this;
    }

    public MetricBuilder addTag(MetricActions action) {
        this.action = action.getTag();
        return this;
    }

    public MetricBuilder addTag(SupportedLanguages language) {
        this.language = language.getTag();
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
            return INVALID_METRICS_WRAPPER;
        }
    }

    private boolean check() {
        if (isAnyNull(name, action)) {
            return false;
        }

        boolean isGetCourseAction = MetricActions.GET_COURSE.getTag().equals(action);

        if (isGetCourseAction && (courseId == null || !isAllNull(stepId, language))) {
            return false;
        }

        if (!isGetCourseAction && isAnyNull(courseId, stepId, language)) {
            return false;
        }
        return true;
    }
}