package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.recommendations.StepikReactionsPostQuery

class StepikRecommendationReactionsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun post(): StepikReactionsPostQuery {
        return StepikReactionsPostQuery(this)
    }
    
}
