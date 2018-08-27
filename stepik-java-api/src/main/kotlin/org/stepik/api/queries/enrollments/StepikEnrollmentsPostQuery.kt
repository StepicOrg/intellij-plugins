package org.stepik.api.queries.enrollments

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.enrollments.Enrollments
import org.stepik.api.objects.enrollments.EnrollmentsPost
import org.stepik.api.queries.StepikAbstractPostQuery

class StepikEnrollmentsPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<Enrollments>(stepikAction, Enrollments::class.java) {
    
    private val enrollment = EnrollmentsPost()
    
    fun course(id: Long): StepikEnrollmentsPostQuery {
        enrollment.enrollment.course = id
        return this
    }
    
    override val body: String
        get() {
            return jsonConverter.toJson(enrollment, false)
        }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/enrollments"
    
}
