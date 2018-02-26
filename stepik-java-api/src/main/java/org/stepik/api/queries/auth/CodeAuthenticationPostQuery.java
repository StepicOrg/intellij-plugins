package org.stepik.api.queries.auth;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.auth.GrantTypes;


public class CodeAuthenticationPostQuery extends AbstractAuthorizationPostQuery {
    public CodeAuthenticationPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction);
        addParam("grant_type", GrantTypes.AUTHORIZATION_CODE.toString());
    }

    @NotNull
    public CodeAuthenticationPostQuery clientId(@NotNull String clientId) {
        addParam("client_id", clientId);
        return this;
    }

    @NotNull
    public CodeAuthenticationPostQuery code(@NotNull String code) {
        addParam("code", code);
        return this;
    }

    @NotNull
    public CodeAuthenticationPostQuery redirectUri(@NotNull String redirectUri) {
        addParam("redirect_uri", redirectUri);
        return this;
    }
}
