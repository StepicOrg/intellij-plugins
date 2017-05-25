package org.stepik.core.stepik;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.util.net.HttpConfigurable;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.HttpTransportClient;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.auth.TokenInfo;
import org.stepik.api.objects.users.User;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.utils.PluginUtils;
import org.stepik.core.utils.ProductGroup;
import org.stepik.plugin.auth.ui.AuthDialog;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.stepik.StepikAuthState.AUTH;
import static org.stepik.core.stepik.StepikAuthState.NOT_AUTH;
import static org.stepik.core.stepik.StepikAuthState.SHOW_DIALOG;
import static org.stepik.core.stepik.StepikAuthState.UNKNOWN;
import static org.stepik.core.utils.PluginUtils.PLUGIN_ID;
import static org.stepik.core.utils.PluginUtils.getCurrentProduct;
import static org.stepik.core.utils.PluginUtils.getCurrentProductVersion;
import static org.stepik.core.utils.PluginUtils.getVersion;

public class StepikAuthManager {
    private static final Logger logger = Logger.getInstance(StepikAuthManager.class);
    private static final String CLIENT_ID = "vV8giW7KTPMOTriOUBwyGLvXbKV0Cc4GPBnyCJPd";
    private static final String CLIENT_ID_PASSWORD_BASED = "MVo2c17mATXWfBUdmP5oCsFrUB7RtCYqOvUmng90";
    private static final String REDIRECT_URI = "https%3A%2F%2Fstepik.org";
    private static final String LAST_USER_PROPERTY_NAME = PLUGIN_ID + ".LAST_USER";
    private static final StepikApiClient stepikApiClient = initStepikApiClient();
    private static final String IMPLICIT_GRANT_URL = "https://stepik.org/oauth2/authorize/" +
            "?client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_URI +
            "&scope=write" +
            "&response_type=token";
    private static final List<StepikAuthManagerListener> listeners = new ArrayList<>();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static volatile StepikAuthState state = UNKNOWN;
    private static User user;

    private static long getLastUser() {
        return PropertiesComponent.getInstance().getOrInitLong(LAST_USER_PROPERTY_NAME, 0);
    }

    private static void setLastUser(long userId) {
        PropertiesComponent.getInstance().setValue(LAST_USER_PROPERTY_NAME, String.valueOf(userId));
    }

    @NotNull
    private static synchronized StepikApiClient initStepikApiClient() {
        String osName = System.getProperty("os.name");
        String jre = System.getProperty("java.version");
        String userAgent = String.format("Stepik Union/%s/%s (%s) %s/%s JRE/%s",
                getVersion(),
                StepikApiClient.getVersion(),
                osName,
                getCurrentProduct(),
                getCurrentProductVersion(),
                jre);
        logger.info(userAgent);

        HttpConfigurable instance = HttpConfigurable.getInstance();
        StepikApiClient client;
        if (instance.USE_HTTP_PROXY) {
            logger.info(String.format("Uses proxy: Host = %s, Port = %s", instance.PROXY_HOST, instance.PROXY_PORT));
            HttpTransportClient transportClient;
            transportClient = HttpTransportClient.getInstance(instance.PROXY_HOST, instance.PROXY_PORT, userAgent);
            client = new StepikApiClient(transportClient);
        } else {
            client = new StepikApiClient(userAgent);
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
    public static synchronized StepikAuthState authentication(boolean showDialog) {
        StepikAuthState value = minorLogin();
        if (value != AUTH && showDialog) {
            setState(SHOW_DIALOG);
            value = showAuthDialog();
        }
        setState(value);
        return value;
    }

    @NotNull
    private static StepikAuthState showAuthDialog() {
        Application application = ApplicationManager.getApplication();
        boolean isDispatchThread = application.isDispatchThread() || SwingUtilities.isEventDispatchThread();

        final StepikAuthState[] authenticated = new StepikAuthState[]{state};

        Runnable showDialog = () -> authenticated[0] = showDialog();

        if (!isDispatchThread) {
            try {
                if (PluginUtils.isCurrent(ProductGroup.PYCHARM)) {
                    /* TODO Check it what it's actual for supported versions today.
                    * For some reason PyCharm run in Swing Event Dispatch Thread
                    */
                    SwingUtilities.invokeAndWait(showDialog);
                } else {
                    application.invokeAndWait(showDialog, ModalityState.defaultModalityState());
                }
            } catch (InterruptedException | InvocationTargetException | ProcessCanceledException e) {
                logger.warn(e);
            }
        } else {
            showDialog.run();
        }

        return authenticated[0];
    }

    @NotNull
    private static StepikAuthState showDialog() {
        Map<String, String> map = AuthDialog.showAuthForm();
        StepikAuthState newState = NOT_AUTH;
        TokenInfo tokenInfo = new TokenInfo();
        if (!map.isEmpty() && !map.containsKey("error")) {
            newState = AUTH;
            tokenInfo.setAccessToken(map.get("access_token"));
            tokenInfo.setExpiresIn(Integer.valueOf(map.getOrDefault("expires_in", "0")));
            tokenInfo.setScope(map.get("scope"));
            tokenInfo.setTokenType(map.get("token_type"));
            tokenInfo.setRefreshToken(map.get("refresh_token"));
        }

        stepikApiClient.setTokenInfo(tokenInfo);
        if (newState == AUTH && tokenInfo.getAccessToken() != null) {
            User user = getCurrentUser(true);
            if (!user.isGuest()) {
                setTokenInfo(user.getId(), tokenInfo);
            } else {
                newState = NOT_AUTH;
            }
        }

        logger.info("Show the authentication dialog with result: " + newState);

        return newState;
    }

    public static boolean isAuthenticated() {
        if (state == NOT_AUTH) {
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

    private static void setState(@NotNull StepikAuthState value) {
        StepikAuthState oldState = state;
        state = value;

        if (state == NOT_AUTH) {
            stepikApiClient.setTokenInfo(null);
            user = null;
            long userId = getLastUser();
            setTokenInfo(userId, new TokenInfo());
            setLastUser(0);
        }

        if (oldState != state) {
            if (state == AUTH) {
                Metrics.authenticate(SUCCESSFUL);
            }
            executor.execute(() ->
                    listeners.forEach(listener -> listener.stateChanged(oldState, state)));
        }
    }

    @NotNull
    private static StepikAuthState minorLogin() {
        if (isAuthenticated()) {
            return AUTH;
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
                return AUTH;
            } catch (StepikClientException re) {
                logger.info("Refresh a token failed: " + re.getMessage());
            }
        }

        return NOT_AUTH;
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
        Credentials credentials = PasswordSafe.getInstance().get(attributes);
        TokenInfo authInfo = null;
        if (credentials != null) {
            String password = credentials.getPasswordAsString();
            authInfo = client.getJsonConverter().fromJson(password, TokenInfo.class);
        }
        if (authInfo == null) {
            return new TokenInfo();
        }
        return authInfo;
    }

    private static void setTokenInfo(long userId, @NotNull final TokenInfo tokenInfo) {
        String serviceName = StepikProjectManager.class.getName();
        String userName = String.valueOf(userId);
        CredentialAttributes attributes;
        attributes = new CredentialAttributes(serviceName, userName, StepikProjectManager.class, false);
        String serializedAuthInfo = stepikApiClient.getJsonConverter().toJson(tokenInfo);
        Credentials credentials = new Credentials(attributes.getUserName(), serializedAuthInfo);
        PasswordSafe.getInstance().set(attributes, credentials);
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
        return (user.getFirstName() + " " + user.getLastName()).trim();
    }

    public static StepikApiClient authAndGetStepikApiClient() {
        return authAndGetStepikApiClient(false);
    }

    public static StepikApiClient authAndGetStepikApiClient(boolean showDialog) {
        StepikAuthManager.authentication(showDialog);
        return stepikApiClient;
    }

    public static synchronized void logout() {
        setState(NOT_AUTH);
        logger.info("Logout successfully");
    }

    @NotNull
    public static String getImplicitGrantUrl() {
        return IMPLICIT_GRANT_URL;
    }

    public static synchronized void relogin() {
        logout();
        setState(showAuthDialog());
    }

    public static void addListener(@NotNull StepikAuthManagerListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(@NotNull StepikAuthManagerListener listener) {
        listeners.remove(listener);
    }

    public static synchronized StepikAuthState authentication(@NotNull String email, @NotNull String password) {
        try {
            TokenInfo tokenInfo = stepikApiClient.oauth2()
                    .userAuthenticationPassword(CLIENT_ID_PASSWORD_BASED, email, password)
                    .execute();
            stepikApiClient.setTokenInfo(tokenInfo);
            User user = getCurrentUser(true);
            setTokenInfo(user.getId(), tokenInfo);
            setState(AUTH);
        } catch (StepikClientException e) {
            logger.warn(e);
            setState(NOT_AUTH);
        }

        return state;
    }
}
