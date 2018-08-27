package org.stepik.api.queries.users

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.users.Users
import org.stepik.api.queries.Order
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikUsersGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikUsersGetQuery, Users>(stepikAction, Users::class.java) {
    
    fun page(page: Int): StepikUsersGetQuery {
        addParam("page", page)
        return this
    }
    
    fun alias(value: String): StepikUsersGetQuery {
        addParam("alias", value)
        return this
    }
    
    fun order(value: Order): StepikUsersGetQuery {
        addParam("order", value.toString())
        return this
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/users"
    
}
