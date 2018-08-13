package org.stepik.api.queries.units

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.units.Units
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikUnitsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikUnitsGetQuery, Units>(stepikAction, Units::class.java) {
    
    fun page(value: Int): StepikUnitsGetQuery {
        addParam("page", value)
        return this
    }
    
    fun lesson(value: Long): StepikUnitsGetQuery {
        addParam("lesson", value)
        return this
    }
    
    override fun getUrl(): String {
        return "${stepikAction.stepikApiClient.host}/api/units"
    }
}
