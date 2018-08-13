package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.users.HSUsersGetQuery

class HSUsersAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {

    fun get(): HSUsersGetQuery {
        return HSUsersGetQuery(this)
    }
}
