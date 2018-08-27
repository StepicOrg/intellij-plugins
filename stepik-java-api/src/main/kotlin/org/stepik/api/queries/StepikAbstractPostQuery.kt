package org.stepik.api.queries

import org.stepik.api.actions.StepikAbstractAction

abstract class StepikAbstractPostQuery<T> protected constructor(stepikAction: StepikAbstractAction,
                                                                responseClass: Class<T>) :
        StepikAbstractQuery<T>(stepikAction, responseClass, QueryMethod.POST) {
    
    override val contentType: String = "application/json"
}
