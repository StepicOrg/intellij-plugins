package com.jetbrains.tmp.learning.stepik;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.net.HttpConfigurable;
import com.jetbrains.tmp.learning.StepikProjectManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.HttpTransportClient;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.auth.TokenInfo;
import org.stepik.api.objects.users.User;
import org.stepik.core.metrics.Metrics;
import org.stepik.plugin.auth.ui.AuthDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.utils.PluginUtils.PLUGIN_ID;

public class StepikConnectorLogin {
    private static final Logger logger = Logger.getInstance(StepikConnectorLogin.class);
    private static final String CLIENT_ID = "vV8giW7KTPMOTriOUBwyGLvXbKV0Cc4GPBnyCJPd";
    private static final String REDIRECT_URI = "https://stepik.org";
    private static final String LAST_USER_PROPERTY_NAME = PLUGIN_ID + ".LAST_USER";
    private static final StepikApiClient stepikApiClient = initStepikApiClient();
    private static final String IMPLICIT_GRANT_URL_TEMPLATE = "https://stepik.org/oauth2/authorize/" +
            "?client_id=" + CLIENT_ID +
            "&redirect_uri=%s" +
            "&scope=write" +
            "&state=%s" +
            "&response_type=token";

    private static long getLastUser() {
        return PropertiesComponent.getInstance().getOrInitLong(LAST_USER_PROPERTY_NAME, 0);
    }

    private static void setLastUser(long userId) {
        PropertiesComponent.getInstance().setValue(LAST_USER_PROPERTY_NAME, String.valueOf(userId));
    }

    @NotNull
    private static StepikApiClient initStepikApiClient() {
        HttpConfigurable instance = HttpConfigurable.getInstance();
        StepikApiClient client;
        if (instance.USE_HTTP_PROXY) {
            logger.info("Uses proxy: Host = " + instance.PROXY_HOST + " Port = " + instance.PROXY_PORT);
            HttpTransportClient transportClient;
            transportClient = HttpTransportClient.getInstance(instance.PROXY_HOST, instance.PROXY_PORT);
            client = new StepikApiClient(transportClient);
        } else {
            client = new StepikApiClient();
        }

        long lastUserId = getLastUser();

        AuthInfo authInfo = getAuthInfo(lastUserId, client);

        client.setTokenInfo(authInfo.getTokenInfo());

        return client;
    }

    /**
     * Authentication is in the following order:
     * <ul>
     * <li>Check a current authentication.</li>
     * <li>Try refresh a token.</li>
     * <li>Try authentication with a stored password.</li>
     * <li>Show a dialog box for getting an username and a password</li>
     * </ul>
     */
    public static void authentication() {
        AuthInfo authInfo = getAuthInfo(getLastUser());
        if (!minorLogin(authInfo)) {
            showAuthDialog(false);
        }
    }

    private static void showAuthDialog(boolean clear) {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            logger.info("Show the authentication dialog");
            Map<String, String> map = AuthDialog.showAuthForm(clear);

            TokenInfo tokenInfo = stepikApiClient.getTokenInfo();
            tokenInfo.setAccessToken(map.get("access_token"));
            tokenInfo.setExpiresIn(Integer.valueOf(map.getOrDefault("expires_in", "0")));
            tokenInfo.setScope(map.get("scope"));
            tokenInfo.setTokenType(map.get("token_type"));
            tokenInfo.setRefreshToken(null);
        }, ModalityState.defaultModalityState());
    }

    private static boolean minorLogin(@NotNull AuthInfo authInfo) {
        logger.info("Check the authentication");

        if (stepikApiClient.getTokenInfo().getAccessToken() != null) {
            User user = getCurrentUser();

            if (!user.isGuest()) {
                logger.info("Authenticated");
                return true;
            }
        }

        String refreshToken = stepikApiClient.getTokenInfo().getRefreshToken();
        if (refreshToken == null && authInfo.getTokenInfo() != null) {
            refreshToken = authInfo.getTokenInfo().getRefreshToken();
        }

        if (refreshToken != null) {
            try {
                logger.info("Try refresh a token");
                stepikApiClient.oauth2()
                        .userAuthenticationRefresh(CLIENT_ID, refreshToken)
                        .execute();
                logger.info("Refresh a token is successfully");
                return true;
            } catch (StepikClientException re) {
                logger.info("Refresh a token failed: " + re.getMessage());
            }
        }

        if (authInfo.canBeValid()) {
            try {
                logger.info("Try execute the Authentication with a password");
                authenticate(authInfo.getUsername(), authInfo.getPassword());
                logger.info("The Authentication with a password is successfully");
                return true;
            } catch (StepikClientException e) {
                logger.info("The Authentication with a password failed: " + e.getMessage());
            }
        }

        return false;
    }

    @NotNull
    private static AuthInfo getAuthInfo(long userId) {
        return getAuthInfo(userId, stepikApiClient);
    }

    @NotNull
    private static AuthInfo getAuthInfo(long userId, StepikApiClient client) {
        if (userId == 0) {
            return new AuthInfo();
        }
        String serviceName = StepikProjectManager.class.getName();
        CredentialAttributes attributes = new CredentialAttributes(serviceName,
                String.valueOf(userId),
                StepikProjectManager.class,
                false);
        String serializedAuthInfo;
        synchronized (StepikConnectorLogin.class) {
            serializedAuthInfo = PasswordSafe.getInstance().getPassword(attributes);
        }
        AuthInfo authInfo = client.getJsonConverter().fromJson(serializedAuthInfo, AuthInfo.class);

        if (authInfo == null) {
            return new AuthInfo();
        }
        return authInfo;
    }

    @NotNull
    public static String getCurrentUserPassword() {
        User currentUser = getCurrentUser();
        return getAuthInfo(currentUser.getId()).getPassword();
    }

    private static void setAuthInfo(long userId, @NotNull final AuthInfo authInfo) {
        String serviceName = StepikProjectManager.class.getName();
        CredentialAttributes attributes = new CredentialAttributes(serviceName,
                String.valueOf(userId),
                StepikProjectManager.class,
                false);
        String serializedAuthInfo = stepikApiClient.getJsonConverter().toJson(authInfo);
        synchronized (StepikConnectorLogin.class) {
            PasswordSafe.getInstance().setPassword(attributes, serializedAuthInfo);
            setLastUser(userId);
        }
    }

    @NotNull
    public static StepikApiClient getStepikApiClient() {
        return stepikApiClient;
    }

    @NotNull
    public static User testAuthentication(@Nullable String username, @Nullable String password) {
        TokenInfo currentTokenInfo = stepikApiClient.getTokenInfo();

        User testUser;
        try {
            authenticate(username, password);
            logger.info("The test authentication is successfully");
            testUser = getCurrentUser();
        } catch (StepikClientException e) {
            logger.info("The test authentication failed");
            throw e;
        } finally {
            stepikApiClient.setTokenInfo(currentTokenInfo);
        }

        return testUser;
    }

    @NotNull
    public static User getCurrentUser() {
        try {
            return stepikApiClient.stepiks()
                    .get()
                    .id(1)
                    .execute().getUser();
        } catch (StepikClientException e) {
            logger.warn("Get current user failed", e);
            return new User();
        }
    }

    public static void authenticate(@Nullable String username, @Nullable String password) {
        try {
            stepikApiClient.oauth2()
                    .userAuthenticationPassword(CLIENT_ID, username, password)
                    .execute();

            AuthInfo authInfo = new AuthInfo();
            authInfo.setTokenInfo(stepikApiClient.getTokenInfo());
            authInfo.setUsername(username);
            authInfo.setPassword(password);

            long userId = getCurrentUser().getId();
            setAuthInfo(userId, authInfo);
            logger.info("Authentication is successfully");
            Metrics.authenticate(SUCCESSFUL);
        } catch (StepikClientException e) {
            logger.warn("Authentication failed", e);
            throw e;
        }
    }

    @NotNull
    public static String getCurrentUsername() {
        User currentUser = getCurrentUser();
        return getAuthInfo(currentUser.getId()).getUsername();
    }

    @NotNull
    public static String getCurrentUserFullName() {
        User user = getCurrentUser();
        return user.getFirstName() + " " + user.getLastName();
    }

    public static StepikApiClient authAndGetStepikApiClient() {
        StepikConnectorLogin.authentication();
        return stepikApiClient;
    }

    public static void logout() {
        synchronized (StepikConnectorLogin.class) {
            stepikApiClient.setTokenInfo(null);
            long userId = getLastUser();
            setAuthInfo(userId, new AuthInfo());
            setLastUser(0);
            logger.info("Logout successfully");
        }
    }

    @Nullable
    public static String getImplicitGrantUrl() {
        try {
            return String.format(IMPLICIT_GRANT_URL_TEMPLATE, URLEncoder.encode(REDIRECT_URI, UTF_8.name()), "1234");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static void logoutAndAuth() {
        logout();
        showAuthDialog(true);
    }
}