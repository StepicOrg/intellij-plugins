package org.stepik.api.objects.metrics;

/**
 * @author meanmail
 */
public class MetricWrapper<T> {
    private Metric<T> metric = new Metric<>();

    public Metric<T> getMetric() {
        return metric;
    }
}
