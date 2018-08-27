package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.metrics.StepikMetricsPostQuery

class StepikMetricsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun post(): StepikMetricsPostQuery {
        return StepikMetricsPostQuery(this)
    }
}
