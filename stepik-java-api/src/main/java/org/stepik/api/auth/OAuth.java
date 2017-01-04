package org.stepik.api.auth;

import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class OAuth {
    private final StepikApiClient stepikApiClient;
    private String clientId;
    private String login;
    private String password;

    public OAuth(StepikApiClient stepikApiClient) {
        this.stepikApiClient = stepikApiClient;
    }

    OAuth userAuthorizationPassword(String clientId, String login, String password) {
        this.clientId = clientId;
        this.login = login;
        this.password = password;

        return this;
    }

    public void execute() {

    }
}
