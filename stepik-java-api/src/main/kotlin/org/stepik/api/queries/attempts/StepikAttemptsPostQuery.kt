package org.stepik.api.queries.attempts

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.attempts.Attempts
import org.stepik.api.objects.attempts.AttemptsPost
import org.stepik.api.queries.StepikAbstractPostQuery

class StepikAttemptsPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<Attempts>(stepikAction, Attempts::class.java) {
    
    private val attempts = AttemptsPost()
    
    fun step(id: Long): StepikAttemptsPostQuery {
        attempts.attempt.step = id
        return this
    }
    
    override fun getBody(): String {
        return jsonConverter.toJson(attempts, false)
    }
    
    override fun getUrl(): String {
        return "${stepikAction.stepikApiClient.host}/api/attempts"
    }
}
