package org.stepik.api.queries.enrollments

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.enrollments.Enrollments
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikEnrollmentsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikEnrollmentsGetQuery, Enrollments>(stepikAction, Enrollments::class.java) {
    
    fun page(value: Int): StepikEnrollmentsGetQuery {
        addParam("page", value)
        return this
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/enrollments"
    
}
