package org.stepik.api.queries.auth;

import com.sun.istack.internal.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.auth.GrantTypes;
import org.stepik.api.objects.auth.TokenInfo;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class UserAuthorizationQuery extends StepikAbstractPostQuery<TokenInfo> {
    public UserAuthorizationQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, TokenInfo.class);
    }

    public UserAuthorizationQuery clientId(String clientId) {
        addParam("client_id", clientId);
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.OAUTH_URL;
    }

    public UserAuthorizationQuery login(String login) {
        addParam("username", login);
        return this;
    }

    public UserAuthorizationQuery password(String password) {
        addParam("password", password);
        return this;
    }

    public UserAuthorizationQuery grantType(GrantTypes grantType) {
        addParam("grant_type", grantType.toString());
        return this;
    }

    public UserAuthorizationQuery refreshToken(String refreshToken) {
        addParam("refresh_token", refreshToken);
        return this;
    }

    @Override
    public TokenInfo execute() {
        TokenInfo tokenInfo = super.execute();

        StepikAbstractAction action = getStepikAction();
        action.getStepikApiClient().setTokenInfo(tokenInfo);

        return tokenInfo;
    }

    @Override
    protected String getContentType() {
        return "application/x-www-form-urlencoded";
    }
}
