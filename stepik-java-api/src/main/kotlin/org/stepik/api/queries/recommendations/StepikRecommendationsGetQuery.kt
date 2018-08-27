package org.stepik.api.queries.recommendations

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.recommendations.Recommendations
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikRecommendationsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikRecommendationsGetQuery, Recommendations>(stepikAction,
                Recommendations::class.java) {
    
    fun course(course: Long): StepikRecommendationsGetQuery {
        addParam("course", course)
        return this
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/recommendations"
    
    override fun isCacheEnabled() = false
}
