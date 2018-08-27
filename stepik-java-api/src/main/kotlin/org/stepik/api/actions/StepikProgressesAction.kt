package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.progresses.StepikProgressesGetQuery

class StepikProgressesAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikProgressesGetQuery {
        return StepikProgressesGetQuery(this)
    }
}
