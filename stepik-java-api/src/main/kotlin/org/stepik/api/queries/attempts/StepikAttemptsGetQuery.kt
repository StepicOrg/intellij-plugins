package org.stepik.api.queries.attempts

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.attempts.Attempts
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikAttemptsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikAttemptsGetQuery, Attempts>(stepikAction, Attempts::class.java) {
    
    fun step(id: Long): StepikAttemptsGetQuery {
        addParam("step", id)
        return this
    }
    
    fun user(userId: Long): StepikAttemptsGetQuery {
        addParam("user", userId)
        return this
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/attempts"
    
}
