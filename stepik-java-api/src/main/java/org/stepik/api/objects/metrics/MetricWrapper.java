package org.stepik.api.objects.metrics;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class MetricWrapper<T> {
    private Metric metric = new Metric();

    @NotNull
    public Metric getMetric() {
        if (metric == null) {
            metric = new Metric();
        }
        return metric;
    }
}
