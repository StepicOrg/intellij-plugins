package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.users.StepikUsersGetQuery

class StepikUsersAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikUsersGetQuery {
        return StepikUsersGetQuery(this)
    }
}
