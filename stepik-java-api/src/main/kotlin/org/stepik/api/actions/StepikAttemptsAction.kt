package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.attempts.StepikAttemptsGetQuery
import org.stepik.api.queries.attempts.StepikAttemptsPostQuery

class StepikAttemptsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun post(): StepikAttemptsPostQuery {
        return StepikAttemptsPostQuery(this)
    }
    
    fun get(): StepikAttemptsGetQuery {
        return StepikAttemptsGetQuery(this)
    }
}
