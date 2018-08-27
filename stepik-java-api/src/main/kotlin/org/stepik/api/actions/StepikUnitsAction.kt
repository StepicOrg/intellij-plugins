package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.units.StepikUnitsGetQuery

class StepikUnitsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikUnitsGetQuery {
        return StepikUnitsGetQuery(this)
    }
}
