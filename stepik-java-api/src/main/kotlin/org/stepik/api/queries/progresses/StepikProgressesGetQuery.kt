package org.stepik.api.queries.progresses

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.progresses.Progresses
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikProgressesGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikProgressesGetQuery, Progresses>(stepikAction, Progresses::class.java) {
    
    override fun getUrl(): String {
        return "${stepikAction.stepikApiClient.host}/api/progresses"
    }
    
    override fun isCacheEnabled(): Boolean {
        return false
    }
    
    fun page(page: Int): StepikProgressesGetQuery {
        addParam("page", page)
        return this
    }
}
