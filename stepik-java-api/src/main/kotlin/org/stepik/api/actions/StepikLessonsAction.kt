package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.lessons.StepikLessonsGetQuery

class StepikLessonsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikLessonsGetQuery {
        return StepikLessonsGetQuery(this)
    }
}
