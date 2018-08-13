package org.stepik.api.queries.auth

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.auth.GrantTypes

class CodeAuthenticationPostQuery(stepikAction: StepikAbstractAction) : AbstractAuthorizationPostQuery(stepikAction) {
    init {
        addParam("grant_type", GrantTypes.AUTHORIZATION_CODE.toString())
    }
    
    fun clientId(clientId: String): CodeAuthenticationPostQuery {
        addParam("client_id", clientId)
        return this
    }
    
    fun code(code: String): CodeAuthenticationPostQuery {
        addParam("code", code)
        return this
    }
    
    fun redirectUri(redirectUri: String): CodeAuthenticationPostQuery {
        addParam("redirect_uri", redirectUri)
        return this
    }
}
