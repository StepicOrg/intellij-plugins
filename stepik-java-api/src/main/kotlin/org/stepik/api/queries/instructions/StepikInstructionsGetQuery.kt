package org.stepik.api.queries.instructions

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.instructions.Instructions
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikInstructionsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikInstructionsGetQuery, Instructions>(stepikAction, Instructions::class.java) {
    
    fun page(page: Int): StepikInstructionsGetQuery {
        addParam("page", page)
        return this
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/instructions"
    
}
