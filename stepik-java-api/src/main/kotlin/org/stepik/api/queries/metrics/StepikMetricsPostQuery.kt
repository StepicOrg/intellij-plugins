package org.stepik.api.queries.metrics

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.metrics.MetricWrapper
import org.stepik.api.queries.StepikAbstractPostQuery
import org.stepik.api.queries.VoidResult

class StepikMetricsPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<VoidResult>(stepikAction, VoidResult::class.java) {
    
    private val metrics = MetricWrapper()
    
    fun name(name: String): StepikMetricsPostQuery {
        metrics.getMetric()
                .name = name
        return this
    }
    
    fun timestamp(value: Long): StepikMetricsPostQuery {
        metrics.getMetric()
                .timestamp = value
        return this
    }
    
    fun tags(value: Map<String, String>): StepikMetricsPostQuery {
        metrics.getMetric()
                .tags = value.toMutableMap()
        return this
    }
    
    fun tags(key: String, value: String): StepikMetricsPostQuery {
        metrics.getMetric()
                .addTags(key, value)
        return this
    }
    
    fun tags(key: String, value: Any): StepikMetricsPostQuery {
        metrics.getMetric()
                .addTags(key, value.toString())
        return this
    }
    
    fun data(value: Map<String, Any>): StepikMetricsPostQuery {
        metrics.getMetric()
                .data = value.toMutableMap()
        return this
    }
    
    fun data(key: String, value: Any): StepikMetricsPostQuery {
        metrics.getMetric()
                .addData(key, value)
        return this
    }
    
    override fun getBody(): String {
        return jsonConverter.toJson(metrics, false)
    }
    
    override fun getUrl(): String {
        return "${stepikAction.stepikApiClient.host}/api/metrics"
    }
    
    override fun toString(): String {
        return jsonConverter.toJson(metrics, false)
    }
}
