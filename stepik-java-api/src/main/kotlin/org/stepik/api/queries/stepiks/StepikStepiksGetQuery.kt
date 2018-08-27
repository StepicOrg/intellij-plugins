package org.stepik.api.queries.stepiks

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.stepiks.Stepiks
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikStepiksGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikStepiksGetQuery, Stepiks>(stepikAction, Stepiks::class.java) {
    
    private var id: Int = 0
    
    fun id(value: Int): StepikStepiksGetQuery {
        id = value
        return this
    }
    
    override fun isCacheEnabled() = false
    
    override val url = "${stepikAction.stepikApiClient.host}/api/stepics/$id"
    
}
