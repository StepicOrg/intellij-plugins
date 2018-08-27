package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.ObjectsContainer
import org.stepik.api.queries.attempts.StepikAttemptsGetQuery
import org.stepik.api.queries.attempts.StepikAttemptsPostQuery

class StepikAttemptsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun <R : ObjectsContainer<*>> post(): StepikAttemptsPostQuery {
        return StepikAttemptsPostQuery(this)
    }
    
    fun <R : ObjectsContainer<*>> get(): StepikAttemptsGetQuery {
        return StepikAttemptsGetQuery(this)
    }
}
