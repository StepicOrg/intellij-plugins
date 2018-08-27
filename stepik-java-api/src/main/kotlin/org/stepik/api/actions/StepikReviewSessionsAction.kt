package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.ObjectsContainer
import org.stepik.api.queries.reviews.StepikReviewSessionsPostQuery

class StepikReviewSessionsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun <R : ObjectsContainer<*>> post(): StepikReviewSessionsPostQuery {
        return StepikReviewSessionsPostQuery(this)
    }
}
