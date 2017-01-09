package org.stepik.api.auth;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.auth.UserAuthorizationRefreshPostQuery;
import org.stepik.api.queries.auth.UserPasswordAuthorizationPostQuery;

/**
 * @author meanmail
 */
public class OAuth2 extends StepikAbstractAction {

    public OAuth2(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public UserPasswordAuthorizationPostQuery userAuthorizationPassword(
            String clientId,
            String login,
            String password) {
        UserPasswordAuthorizationPostQuery query = new UserPasswordAuthorizationPostQuery(this);

        query.clientId(clientId);
        query.username(login);
        query.password(password);

        return query;
    }

    @NotNull
    public UserAuthorizationRefreshPostQuery userAuthorizationRefresh(String clientId, String refreshToken) {
        UserAuthorizationRefreshPostQuery query = new UserAuthorizationRefreshPostQuery(this);

        query.clientId(clientId);
        query.refreshToken(refreshToken);

        return query;
    }
}
