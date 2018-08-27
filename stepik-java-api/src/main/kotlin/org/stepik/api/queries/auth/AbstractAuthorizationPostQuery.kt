package org.stepik.api.queries.auth

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.auth.TokenInfo
import org.stepik.api.queries.StepikAbstractPostQuery

abstract class AbstractAuthorizationPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<TokenInfo>(stepikAction, TokenInfo::class.java) {
    
    override val url = "${stepikAction.stepikApiClient.host}/oauth2/token/"
    
    override fun execute(): TokenInfo {
        val tokenInfo = super.execute()
        
        val action = stepikAction
        action.stepikApiClient.tokenInfo = tokenInfo
        
        return tokenInfo
    }
    
    override val contentType = "application/x-www-form-urlencoded"
    
}
