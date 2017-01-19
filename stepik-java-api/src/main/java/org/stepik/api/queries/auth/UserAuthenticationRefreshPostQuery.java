package org.stepik.api.queries.auth;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.auth.GrantTypes;

/**
 * @author meanmail
 */
public class UserAuthenticationRefreshPostQuery extends AbstractAuthorizationPostQuery {
    public UserAuthenticationRefreshPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction);
        addParam("grant_type", GrantTypes.REFRESH_TOKEN.toString());
    }

    @NotNull
    public UserAuthenticationRefreshPostQuery clientId(@NotNull String clientId) {
        addParam("client_id", clientId);
        return this;
    }

    @NotNull
    public UserAuthenticationRefreshPostQuery refreshToken(@NotNull String refreshToken) {
        addParam("refresh_token", refreshToken);
        return this;
    }
}
