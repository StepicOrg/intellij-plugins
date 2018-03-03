package org.stepik.api.auth;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.auth.CodeAuthenticationPostQuery;
import org.stepik.api.queries.auth.UserAuthenticationRefreshPostQuery;
import org.stepik.api.queries.auth.UserPasswordAuthenticationPostQuery;

/**
 * @author meanmail
 */
public class OAuth2 extends StepikAbstractAction {

    public OAuth2(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public UserPasswordAuthenticationPostQuery userAuthenticationPassword(
            String clientId,
            String login,
            String password) {
        UserPasswordAuthenticationPostQuery query = new UserPasswordAuthenticationPostQuery(this);

        query.clientId(clientId);
        query.username(login);
        query.password(password);

        return query;
    }

    @NotNull
    public CodeAuthenticationPostQuery userAuthenticationCode(
            String clientId,
            String redirectUri,
            String code) {
        CodeAuthenticationPostQuery query = new CodeAuthenticationPostQuery(this);

        query.clientId(clientId);
        query.redirectUri(redirectUri);
        query.code(code);

        return query;
    }

    @NotNull
    public UserAuthenticationRefreshPostQuery userAuthenticationRefresh(String clientId, String refreshToken) {
        UserAuthenticationRefreshPostQuery query = new UserAuthenticationRefreshPostQuery(this);

        query.clientId(clientId);
        query.refreshToken(refreshToken);

        return query;
    }
}
