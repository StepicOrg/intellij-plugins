package org.stepik.api.auth

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.client.StepikApiClient
import org.stepik.api.queries.auth.CodeAuthenticationPostQuery
import org.stepik.api.queries.auth.UserAuthenticationRefreshPostQuery

class OAuth2(stepikApiClient: StepikApiClient) : StepikAbstractAction(stepikApiClient) {
    
    fun userAuthenticationCode(
            clientId: String,
            redirectUri: String,
            code: String): CodeAuthenticationPostQuery {
        val query = CodeAuthenticationPostQuery(this)
        
        query.clientId(clientId)
        query.redirectUri(redirectUri)
        query.code(code)
        
        return query
    }
    
    fun userAuthenticationRefresh(clientId: String, refreshToken: String): UserAuthenticationRefreshPostQuery {
        val query = UserAuthenticationRefreshPostQuery(this)
        
        query.clientId(clientId)
        query.refreshToken(refreshToken)
        
        return query
    }
}
