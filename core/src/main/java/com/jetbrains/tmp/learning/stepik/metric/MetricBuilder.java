package com.jetbrains.tmp.learning.stepik.metric;

import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.SupportedLanguages;

import static com.jetbrains.tmp.learning.stepik.metric.MetricUtils.isAllNull;
import static com.jetbrains.tmp.learning.stepik.metric.MetricUtils.isAnyNull;

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
        if (isAnyNull(name, action)) {
            return false;
        }
        if (MetricActions.GET_COURSE.toString().equals(action) && (courseId == null || !isAllNull(stepId, language))) {
            return false;
        }

        if (!MetricActions.GET_COURSE.toString().equals(action) && isAnyNull(courseId, stepId, language)) {
            return false;
        }
        return true;
    }
}