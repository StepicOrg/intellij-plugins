package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.steps.StepikStepsGetQuery

class StepikStepsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikStepsGetQuery {
        return StepikStepsGetQuery(this)
    }
}
