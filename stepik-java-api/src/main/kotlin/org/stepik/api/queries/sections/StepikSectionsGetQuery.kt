package org.stepik.api.queries.sections

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.sections.Sections
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikSectionsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikSectionsGetQuery, Sections>(stepikAction, Sections::class.java) {
    
    fun page(value: Int): StepikSectionsGetQuery {
        addParam("page", value)
        return this
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/sections"
    
}
