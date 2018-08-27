package org.stepik.api.queries.submissions

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.submissions.ReviewStatus
import org.stepik.api.objects.submissions.Submissions
import org.stepik.api.queries.Order
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikSubmissionsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikSubmissionsGetQuery, Submissions>(stepikAction, Submissions::class.java) {
    
    fun status(value: String): StepikSubmissionsGetQuery {
        addParam("status", value)
        return this
    }
    
    fun userName(value: String): StepikSubmissionsGetQuery {
        addParam("user_name", value)
        return this
    }
    
    fun step(value: Long): StepikSubmissionsGetQuery {
        addParam("step", value)
        return this
    }
    
    fun user(value: Long): StepikSubmissionsGetQuery {
        addParam("user", value)
        return this
    }
    
    fun attempt(value: Long): StepikSubmissionsGetQuery {
        addParam("attempt", value)
        return this
    }
    
    fun search(value: String): StepikSubmissionsGetQuery {
        addParam("search", value)
        return this
    }
    
    fun order(value: Order): StepikSubmissionsGetQuery {
        addParam("order", value.toString())
        return this
    }
    
    fun reviewStatus(value: ReviewStatus): StepikSubmissionsGetQuery {
        addParam("review_status", value.toString())
        return this
    }
    
    fun page(value: Int): StepikSubmissionsGetQuery {
        addParam("page", value)
        return this
    }
    
    override fun isCacheEnabled(): Boolean {
        return false
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/submissions"
    
}
