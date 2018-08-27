package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.sections.StepikSectionsGetQuery

class StepikSectionsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikSectionsGetQuery {
        return StepikSectionsGetQuery(this)
    }
}
