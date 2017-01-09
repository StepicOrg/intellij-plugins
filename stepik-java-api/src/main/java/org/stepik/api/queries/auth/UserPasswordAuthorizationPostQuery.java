package org.stepik.api.queries.auth;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.auth.GrantTypes;

/**
 * @author meanmail
 */
public class UserPasswordAuthorizationPostQuery extends AbstractAuthorizationPostQuery {
    public UserPasswordAuthorizationPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction);
        addParam("grant_type", GrantTypes.PASSWORD.toString());
    }

    @NotNull
    public UserPasswordAuthorizationPostQuery clientId(@NotNull String clientId) {
        addParam("client_id", clientId);
        return this;
    }

    @NotNull
    public UserPasswordAuthorizationPostQuery username(@NotNull String username) {
        addParam("username", username);
        return this;
    }

    @NotNull
    public UserPasswordAuthorizationPostQuery password(@NotNull String password) {
        addParam("password", password);
        return this;
    }
}
