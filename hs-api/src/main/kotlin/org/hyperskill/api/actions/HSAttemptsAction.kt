package org.hyperskill.api.actions

import org.hyperskill.api.queries.attempts.HSAttemptsPostQuery
import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.attempts.StepikAttemptsGetQuery

class HSAttemptsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun post(): HSAttemptsPostQuery {
        return HSAttemptsPostQuery(this)
    }
    
    fun get(): StepikAttemptsGetQuery {
        return StepikAttemptsGetQuery(this)
    }
}
