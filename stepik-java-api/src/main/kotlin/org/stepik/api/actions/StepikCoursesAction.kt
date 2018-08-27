package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.courses.StepikCoursesGetQuery

class StepikCoursesAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikCoursesGetQuery {
        return StepikCoursesGetQuery(this)
    }
}
