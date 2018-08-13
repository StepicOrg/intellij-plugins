package org.stepik.api.queries.auth

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.auth.GrantTypes

class UserAuthenticationRefreshPostQuery(stepikAction: StepikAbstractAction) :
        AbstractAuthorizationPostQuery(stepikAction) {
    
    init {
        addParam("grant_type", GrantTypes.REFRESH_TOKEN.toString())
    }
    
    fun clientId(clientId: String): UserAuthenticationRefreshPostQuery {
        addParam("client_id", clientId)
        return this
    }
    
    fun refreshToken(refreshToken: String): UserAuthenticationRefreshPostQuery {
        addParam("refresh_token", refreshToken)
        return this
    }
}
