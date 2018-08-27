package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.recommendations.StepikRecommendationsGetQuery

class StepikRecommendationsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikRecommendationsGetQuery {
        return StepikRecommendationsGetQuery(this)
    }
}
