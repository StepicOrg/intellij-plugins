package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.enrollments.StepikEnrollmentsGetQuery
import org.stepik.api.queries.enrollments.StepikEnrollmentsPostQuery

class StepikEnrollmentsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikEnrollmentsGetQuery {
        return StepikEnrollmentsGetQuery(this)
    }
    
    fun post(): StepikEnrollmentsPostQuery {
        return StepikEnrollmentsPostQuery(this)
    }
}
