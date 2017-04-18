package org.stepik.api.queries.metrics;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.metrics.MetricWrapper;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.queries.VoidResult;
import org.stepik.api.urls.Urls;

import java.util.Map;

/**
 * @author meanmail
 */
public class StepikMetricsPostQuery extends StepikAbstractPostQuery<VoidResult> {
    private final MetricWrapper metrics = new MetricWrapper();

    public StepikMetricsPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, VoidResult.class);
    }

    @NotNull
    public StepikMetricsPostQuery name(@NotNull String name) {
        metrics.getMetric().setName(name);
        return this;
    }

    @NotNull
    public StepikMetricsPostQuery timestamp(long value) {
        metrics.getMetric().setTimestamp(value);
        return this;
    }

    @NotNull
    public StepikMetricsPostQuery tags(@NotNull Map<String, String> value) {
        metrics.getMetric().setTags(value);
        return this;
    }

    @NotNull
    public StepikMetricsPostQuery tags(@NotNull String key, @NotNull String value) {
        metrics.getMetric().addTags(key, value);
        return this;
    }

    @NotNull
    public StepikMetricsPostQuery tags(@NotNull String key, @NotNull Object value) {
        metrics.getMetric().addTags(key, value.toString());
        return this;
    }

    @NotNull
    public StepikMetricsPostQuery data(@NotNull Map<String, Object> value) {
        metrics.getMetric().setData(value);
        return this;
    }

    @NotNull
    public StepikMetricsPostQuery data(@NotNull String key, @NotNull Object value) {
        metrics.getMetric().addData(key, value);
        return this;
    }

    @NotNull
    @Override
    protected String getBody() {
        return getJsonConverter().toJson(metrics);
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.METRICS;
    }

    @Override
    public String toString() {
        return getJsonConverter().toJson(metrics);
    }
}
