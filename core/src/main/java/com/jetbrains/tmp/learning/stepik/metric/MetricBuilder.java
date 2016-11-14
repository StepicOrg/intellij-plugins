package com.jetbrains.tmp.learning.stepik.metric;

import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.SupportedLanguages;

import static com.jetbrains.tmp.learning.stepik.metric.MetricUtils.isAllNull;
import static com.jetbrains.tmp.learning.stepik.metric.MetricUtils.isAnyNull;

public class MetricBuilder {
    String metricName = EduNames.METRIC_NAME;
    String name;
    String action;
    String language;
    Integer courseId;
    Integer stepId;

    public MetricBuilder() {}

    public MetricBuilder addTagName(PluginNames name) {
        this.name = name.getTag();
        return this;
    }

    public MetricBuilder addTagAction(MetricActions action) {
        this.action = action.getTag();
        return this;
    }

    public MetricBuilder addTagLanguage(SupportedLanguages language) {
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
            return new MetricsWrapper(this);
        } else {
            return MetricsWrapper.getInvalidWrapper();
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