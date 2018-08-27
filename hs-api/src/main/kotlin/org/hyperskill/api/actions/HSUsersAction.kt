package org.hyperskill.api.actions

import org.hyperskill.api.queries.users.HSUsersGetQuery
import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.client.StepikApiClient

class HSUsersAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): HSUsersGetQuery {
        return HSUsersGetQuery(this)
    }
}
