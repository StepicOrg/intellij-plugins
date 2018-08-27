package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.instructions.StepikInstructionsGetQuery

class StepikInstructionsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikInstructionsGetQuery {
        return StepikInstructionsGetQuery(this)
    }
}
