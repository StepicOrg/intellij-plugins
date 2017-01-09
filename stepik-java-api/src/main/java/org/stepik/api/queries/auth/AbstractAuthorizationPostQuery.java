package org.stepik.api.queries.auth;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.auth.TokenInfo;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public abstract class AbstractAuthorizationPostQuery extends StepikAbstractPostQuery<TokenInfo> {
    public AbstractAuthorizationPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, TokenInfo.class);
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.OAUTH_URL;
    }

    @NotNull
    @Override
    public TokenInfo execute() {
        TokenInfo tokenInfo = super.execute();

        StepikAbstractAction action = getStepikAction();
        action.getStepikApiClient().setTokenInfo(tokenInfo);

        return tokenInfo;
    }

    @NotNull
    @Override
    protected String getContentType() {
        return "application/x-www-form-urlencoded";
    }
}
