package org.stepik.api.objects.metrics

class MetricWrapper {
    
    private var metric: Metric? = Metric()
    
    fun getMetric(): Metric {
        if (metric == null) {
            metric = Metric()
        }
        return metric!!
    }
}
