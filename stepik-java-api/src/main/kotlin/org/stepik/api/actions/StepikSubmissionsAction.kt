package org.stepik.api.actions

import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.submissions.StepikSubmissionsGetQuery
import org.stepik.api.queries.submissions.StepikSubmissionsPostQuery

class StepikSubmissionsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikSubmissionsGetQuery {
        return StepikSubmissionsGetQuery(this)
    }
    
    fun post(): StepikSubmissionsPostQuery {
        return StepikSubmissionsPostQuery(this)
    }
}
