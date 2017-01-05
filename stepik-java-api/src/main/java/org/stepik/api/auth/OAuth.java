package org.stepik.api.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.auth.UserAuthorizationQuery;

/**
 * @author meanmail
 */
public class OAuth extends StepikAbstractAction{
    private static final Logger logger = LogManager.getLogger(OAuth.class);

    private String password;
    private GrantTypes grantType;

    public OAuth(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public UserAuthorizationQuery userAuthorizationPassword(String clientId, String login, String password) {
        UserAuthorizationQuery query = new UserAuthorizationQuery(this);

        query.clientId(clientId);
        query.login(login);
        query.password(password);
        query.grantType(GrantTypes.PASSWORD);

        return query;
    }

    public UserAuthorizationQuery userAuthorizationRefresh(String clientId, String refreshToken) {
        UserAuthorizationQuery query = new UserAuthorizationQuery(this);

        query.clientId(clientId);
        query.refreshToken(refreshToken);
        query.grantType(GrantTypes.REFRESH_TOKEN);

        return query;
    }
}
