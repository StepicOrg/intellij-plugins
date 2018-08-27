package org.hyperskill.api.queries.attempts

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.attempts.Attempt
import org.stepik.api.objects.attempts.AttemptsPost
import org.stepik.api.queries.StepikAbstractPostQuery

class HSAttemptsPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<Attempt>(stepikAction, Attempt::class.java) {
    
    private val attempts = AttemptsPost()
    
    fun step(id: Long): HSAttemptsPostQuery {
        attempts.attempt.step = id
        return this
    }
    
    override val body: String
        get () {
            return jsonConverter.toJson(attempts.attempt, false)
        }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/attempts/"
}
