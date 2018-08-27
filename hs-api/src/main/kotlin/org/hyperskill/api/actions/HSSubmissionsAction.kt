package org.hyperskill.api.actions

import org.hyperskill.api.queries.submissions.HSSubmissionsPostQuery
import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.submissions.StepikSubmissionsGetQuery

class HSSubmissionsAction(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun get(): StepikSubmissionsGetQuery {
        return StepikSubmissionsGetQuery(this)
    }
    
    fun post(): HSSubmissionsPostQuery {
        return HSSubmissionsPostQuery(this)
    }
}
