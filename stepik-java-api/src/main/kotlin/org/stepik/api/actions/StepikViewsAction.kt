package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.views.StepikViewsPostQuery

class StepikViewsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun post(): StepikViewsPostQuery {
        return StepikViewsPostQuery(this)
    }
    
}
