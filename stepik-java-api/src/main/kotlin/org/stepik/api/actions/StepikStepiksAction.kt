package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.users.User
import org.stepik.api.queries.stepiks.StepikStepiksGetQuery

class StepikStepiksAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    val currentUser: User
        get() = this.get()
                .id(1)
                .execute().user
    
    fun get(): StepikStepiksGetQuery {
        return StepikStepiksGetQuery(this)
    }
}
