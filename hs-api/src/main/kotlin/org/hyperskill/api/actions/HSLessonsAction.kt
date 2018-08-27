package org.hyperskill.api.actions

import org.hyperskill.api.queries.lessons.HSLessonsGetQuery
import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.client.StepikApiClient

class HSLessonsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): HSLessonsGetQuery {
        return HSLessonsGetQuery(this)
    }
}
