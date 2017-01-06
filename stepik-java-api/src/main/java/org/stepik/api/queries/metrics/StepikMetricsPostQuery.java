package org.stepik.api.queries.metrics;

import com.google.gson.Gson;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.metrics.MetricWrapper;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

import java.util.Map;

/**
 * @author meanmail
 */
public class StepikMetricsPostQuery extends StepikAbstractPostQuery<String> {
    private final MetricWrapper metrics = new MetricWrapper();

    public StepikMetricsPostQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, String.class);
    }

    public StepikMetricsPostQuery name(String name) {
        metrics.getMetric().setName(name);
        return this;
    }

    public StepikMetricsPostQuery timestamp(int value) {
        metrics.getMetric().setTimestamp(value);
        return this;
    }

    public StepikMetricsPostQuery tags(Map<String, String> value) {
        metrics.getMetric().setTags(value);
        return this;
    }

    public StepikMetricsPostQuery tags(String key, String value) {
        metrics.getMetric().addTags(key, value);
        return this;
    }

    public StepikMetricsPostQuery data(Map<String, Object> value) {
        metrics.getMetric().setData(value);
        return this;
    }

    public StepikMetricsPostQuery data(String key, Object value) {
        metrics.getMetric().addData(key, value);
        return this;
    }

    @Override
    protected String getBody() {
        return new Gson().toJson(metrics);
    }

    @Override
    protected String getUrl() {
        return Urls.METRICS;
    }
}
