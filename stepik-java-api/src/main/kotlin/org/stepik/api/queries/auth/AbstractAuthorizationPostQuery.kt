package org.stepik.api.queries.auth

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.auth.TokenInfo
import org.stepik.api.queries.StepikAbstractPostQuery

abstract class AbstractAuthorizationPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<TokenInfo>(stepikAction, TokenInfo::class.java) {
    
    override fun getUrl(): String {
        return "${stepikAction.stepikApiClient.host}/oauth2/token/"
    }
    
    override fun execute(): TokenInfo {
        val tokenInfo = super.execute()
        
        val action = getStepikAction()
        action.stepikApiClient.tokenInfo = tokenInfo
        
        return tokenInfo
    }
    
    override fun getContentType(): String {
        return "application/x-www-form-urlencoded"
    }
}
