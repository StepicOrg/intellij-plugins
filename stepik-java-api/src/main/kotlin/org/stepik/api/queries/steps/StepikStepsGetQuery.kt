package org.stepik.api.queries.steps

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.steps.Steps
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikStepsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikStepsGetQuery, Steps>(stepikAction, Steps::class.java) {
    
    fun page(value: Int): StepikStepsGetQuery {
        addParam("page", value)
        return this
    }
    
    fun lesson(value: Long): StepikStepsGetQuery {
        addParam("lesson", value)
        return this
    }
    
    override val url = "${this.stepikAction.stepikApiClient.host}/api/steps"
    
}
