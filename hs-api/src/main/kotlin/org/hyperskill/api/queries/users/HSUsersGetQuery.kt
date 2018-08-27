package org.hyperskill.api.queries.users

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.users.HSUsers
import org.stepik.api.queries.StepikAbstractGetQuery

class HSUsersGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<HSUsersGetQuery, HSUsers>(stepikAction, HSUsers::class.java) {
    
    fun page(page: Int): HSUsersGetQuery {
        addParam("page", page)
        return this
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/users"
    
}
