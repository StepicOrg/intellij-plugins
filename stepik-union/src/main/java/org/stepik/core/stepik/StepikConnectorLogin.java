package org.stepik.core.stepik;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.net.HttpConfigurable;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.HttpTransportClient;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.auth.TokenInfo;
import org.stepik.api.objects.users.User;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.utils.PluginUtils;
import org.stepik.core.utils.ProductGroup;
import org.stepik.plugin.auth.ui.AuthDialog;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.utils.PluginUtils.PLUGIN_ID;
import static org.stepik.core.utils.Utils.getCurrentProject;

public class StepikConnectorLogin {
    private static final Logger logger = Logger.getInstance(StepikConnectorLogin.class);
    private static final String CLIENT_ID = "vV8giW7KTPMOTriOUBwyGLvXbKV0Cc4GPBnyCJPd";
    private static final String REDIRECT_URI = "https%3A%2F%2Fstepik.org";
    private static final String LAST_USER_PROPERTY_NAME = PLUGIN_ID + ".LAST_USER";
    private static final StepikApiClient stepikApiClient = initStepikApiClient();
    private static final String IMPLICIT_GRANT_URL = "https://stepik.org/oauth2/authorize/" +
            "?client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_URI +
            "&scope=write" +
            "&response_type=token";
    private static volatile boolean authenticated = true;
    private static User user;

    private static long getLastUser() {
        return PropertiesComponent.getInstance().getOrInitLong(LAST_USER_PROPERTY_NAME, 0);
    }

    private static void setLastUser(long userId) {
        PropertiesComponent.getInstance().setValue(LAST_USER_PROPERTY_NAME, String.valueOf(userId));
    }

    @NotNull
    private static synchronized StepikApiClient initStepikApiClient() {
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

        TokenInfo tokenInfo = getTokenInfo(lastUserId, client);

        client.setTokenInfo(tokenInfo);

        return client;
    }

    /**
     * Authentication is in the following order:
     * <ul>
     * <li>Check a current authentication.</li>
     * <li>Try refresh a token.</li>
     * <li>Try authentication with a stored password.</li>
     * <li>Show a browser for authentication or registration</li>
     * </ul>
     */
    public static synchronized void authentication(boolean showDialog) {
        boolean value = minorLogin() || (showDialog && showAuthDialog(false));
        setAuthenticated(value);
    }

    private static void stateChanged(boolean state) {
        Project project = getCurrentProject();
        StepikProjectManager projectManager = StepikProjectManager.getInstance(project);
        if (projectManager != null) {
            StudyNode root = projectManager.getProjectRoot();
            if (root != null) {
                root.resetStatus();
            }
            projectManager.updateSelection();
        }
    }

    private static boolean showAuthDialog(boolean clear) {
        Application application = ApplicationManager.getApplication();
        final boolean[] authenticated = new boolean[1];
        if (!application.isDispatchThread() && !SwingUtilities.isEventDispatchThread()) {
            if (PluginUtils.isCurrent(ProductGroup.PYCHARM)) {
                try {
                    SwingUtilities.invokeAndWait(() -> authenticated[0] = showDialog(clear));
                } catch (InterruptedException | InvocationTargetException e) {
                    logger.warn(e);
                }
            } else {
                application.invokeAndWait(() -> authenticated[0] = showDialog(clear),
                        ModalityState.defaultModalityState());
            }
        } else {
            authenticated[0] = showDialog(clear);
        }

        return authenticated[0];
    }

    private static boolean showDialog(boolean clear) {
        Map<String, String> map = AuthDialog.showAuthForm(clear);
        boolean authenticated = !map.isEmpty() && !map.containsKey("error");

        logger.info("Show the authentication dialog with result: " + authenticated);

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setAccessToken(map.get("access_token"));
        tokenInfo.setExpiresIn(Integer.valueOf(map.getOrDefault("expires_in", "0")));
        tokenInfo.setScope(map.get("scope"));
        tokenInfo.setTokenType(map.get("token_type"));
        tokenInfo.setRefreshToken(map.get("refresh_token"));
        stepikApiClient.setTokenInfo(tokenInfo);
        if (tokenInfo.getAccessToken() != null) {
            User user = getCurrentUser(true);
            if (!user.isGuest()) {
                setTokenInfo(user.getId(), tokenInfo);
                Metrics.authenticate(SUCCESSFUL);
            }
        }

        return authenticated;
    }

    public static boolean isAuthenticated() {
        if (!authenticated) {
            return false;
        }

        if (stepikApiClient.getTokenInfo().getAccessToken() != null) {
            User user = getCurrentUser(true);
            if (!user.isGuest()) {
                return true;
            }
        }

        return false;
    }

    private static void setAuthenticated(boolean value) {
        if (authenticated != value) {
            authenticated = value;
            stateChanged(authenticated);
        }
    }

    private static boolean minorLogin() {
        if (isAuthenticated()) {
            return true;
        }

        String refreshToken = stepikApiClient.getTokenInfo().getRefreshToken();
        if (refreshToken == null) {
            TokenInfo tokenInfo = getTokenInfo(getLastUser());
            refreshToken = tokenInfo.getRefreshToken();
        }

        if (refreshToken != null) {
            try {
                stepikApiClient.oauth2()
                        .userAuthenticationRefresh(CLIENT_ID, refreshToken)
                        .execute();
                logger.info("Refresh a token is successfully");
                return true;
            } catch (StepikClientException re) {
                logger.info("Refresh a token failed: " + re.getMessage());
            }
        }

        return false;
    }

    @NotNull
    private static TokenInfo getTokenInfo(long userId) {
        return getTokenInfo(userId, stepikApiClient);
    }

    @NotNull
    private static TokenInfo getTokenInfo(long userId, StepikApiClient client) {
        if (userId == 0) {
            return new TokenInfo();
        }
        String serviceName = StepikProjectManager.class.getName();
        CredentialAttributes attributes = new CredentialAttributes(serviceName,
                String.valueOf(userId),
                StepikProjectManager.class,
                false);
        String serializedAuthInfo;
        serializedAuthInfo = PasswordSafe.getInstance().getPassword(attributes);
        TokenInfo authInfo = client.getJsonConverter().fromJson(serializedAuthInfo, TokenInfo.class);

        if (authInfo == null) {
            return new TokenInfo();
        }
        return authInfo;
    }

    private static void setTokenInfo(long userId, @NotNull final TokenInfo tokenInfo) {
        String serviceName = StepikProjectManager.class.getName();
        CredentialAttributes attributes = new CredentialAttributes(serviceName,
                String.valueOf(userId),
                StepikProjectManager.class,
                false);
        String serializedAuthInfo = stepikApiClient.getJsonConverter().toJson(tokenInfo);
        PasswordSafe.getInstance().setPassword(attributes, serializedAuthInfo);
        setLastUser(userId);
    }

    @NotNull
    public static StepikApiClient getStepikApiClient() {
        return stepikApiClient;
    }

    @NotNull
    public static User getCurrentUser() {
        return getCurrentUser(false);
    }

    @NotNull
    private static User getCurrentUser(boolean request) {
        if (user == null || user.getId() == 0 || request) {
            try {
                user = stepikApiClient.stepiks().getCurrentUser();
            } catch (StepikClientException e) {
                logger.warn("Get current user failed", e);
                user = new User();
            }
        }

        return user;
    }

    @NotNull
    public static String getCurrentUserFullName() {
        User user = getCurrentUser();
        return user.getFirstName() + " " + user.getLastName();
    }

    public static StepikApiClient authAndGetStepikApiClient() {
        return authAndGetStepikApiClient(false);
    }

    public static StepikApiClient authAndGetStepikApiClient(boolean showDialog) {
        StepikConnectorLogin.authentication(showDialog);
        return stepikApiClient;
    }

    public static synchronized void logout() {
        stepikApiClient.setTokenInfo(null);
        user = null;
        long userId = getLastUser();
        setTokenInfo(userId, new TokenInfo());
        setLastUser(0);
        setAuthenticated(false);
        logger.info("Logout successfully");
    }

    @NotNull
    public static String getImplicitGrantUrl() {
        return IMPLICIT_GRANT_URL;
    }

    public static void logoutAndAuth() {
        logout();
        showAuthDialog(true);
    }
}