package org.stepik.api.queries.reviews

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.reviews.ReviewSessions
import org.stepik.api.objects.reviews.ReviewSessionsPost
import org.stepik.api.queries.StepikAbstractPostQuery

class StepikReviewSessionsPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<ReviewSessions>(stepikAction, ReviewSessions::class.java) {
    
    private val reviewSessions = ReviewSessionsPost()
    
    fun submission(id: Long): StepikReviewSessionsPostQuery {
        reviewSessions.reviewSession.submission = id
        return this
    }
    
    override val body: String
        get () {
            return jsonConverter.toJson(reviewSessions, false)
        }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/review-sessions"
    
}
