package com.jetbrains.tmp.learning.stepik;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.net.HttpConfigurable;
import com.jetbrains.tmp.learning.StepikProjectManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.HttpTransportClient;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.stepiks.Stepiks;

public class StepikConnectorLogin {
    private static final Logger logger = Logger.getInstance(StepikConnectorLogin.class.getName());
    private static final String CLIENT_ID = "hUCWcq3hZHCmz0DKrDtwOWITLcYutzot7p4n59vU";
    private static StepikUser currentUser;
    private static StepikApiClient stepikApiClient = initStepikApiClient();

    @NotNull
    private static StepikApiClient initStepikApiClient() {
        HttpConfigurable instance = HttpConfigurable.getInstance();
        if (instance.USE_HTTP_PROXY) {
            logger.info("Uses proxy: Host = " + instance.PROXY_HOST + " Port = " + instance.PROXY_PORT);
            HttpTransportClient transportClient;
            transportClient = HttpTransportClient.getInstance(instance.PROXY_HOST, instance.PROXY_PORT);
            return new StepikApiClient(transportClient);
        }

        return new StepikApiClient();
    }

    public static boolean loginFromSettings(
            @NotNull final Project project,
            @NotNull StepikUser basicUser) {
        resetClient();
        StepikUser user = minorLogin(basicUser);

        if (user == null) {
            return false;
        } else {
            StepikProjectManager.getInstance(project).setUser(user);

            Project defaultProject = ProjectManager.getInstance().getDefaultProject();
            if (defaultProject != project) {
                StepikProjectManager.getInstance(defaultProject).setUser(user);
            }
            return true;
        }
    }

    public static boolean loginFromDialog(@NotNull final Project project) {
        StepikUser user = StepikProjectManager.getInstance(project).getUser();
        Project defaultProject = ProjectManager.getInstance().getDefaultProject();
        StepikUser defaultUser = StepikProjectManager.getInstance(defaultProject).getUser();

        if (minorLogin(user) == null) {
            if (minorLogin(defaultUser) == null) {
                return showLoginDialog();
            }
        }

        StepikProjectManager.getInstance(project).setUser(currentUser);
        StepikProjectManager.getInstance(defaultProject).setUser(currentUser);

        return true;
    }

    private static boolean showLoginDialog() {
        final boolean[] logged = {false};
        ApplicationManager.getApplication().invokeAndWait(() -> {
            final LoginDialog dialog = new LoginDialog();
            dialog.show();
            logged[0] = dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE;
        }, ModalityState.defaultModalityState());
        return logged[0];
    }

    private static void resetClient() {
        stepikApiClient.reset();
        currentUser = null;
    }

    @Nullable
    static StepikUser minorLogin(@NotNull StepikUser basicUser) {
        String refreshToken = stepikApiClient.getTokenInfo().getRefreshToken();

        Throwable exception = null;

        if (refreshToken != null) {
            try {
                logger.info("Try refresh a token");
                stepikApiClient.oauth()
                        .userAuthorizationRefresh(CLIENT_ID, refreshToken)
                        .execute();
                logger.info("Refresh a token is successfully");
            } catch (StepikClientException e) {
                logger.info("Refresh a token is failed");
                exception = e;
            }
        }

        if (refreshToken == null || exception != null) {
            try {
                logger.info("Try execute the Authorization with a password");
                String password = basicUser.getPassword();
                if (password.isEmpty()) return null;
                stepikApiClient.oauth()
                        .userAuthorizationPassword(CLIENT_ID, basicUser.getEmail(), password)
                        .execute();
                logger.info("The Authorization with a password is successfully");
            } catch (StepikClientException e) {
                logger.info("The Authorization with a password is failed");
                return null;
            }
        }

        currentUser = new StepikUser(basicUser);
        currentUser.setupTokenInfo(stepikApiClient.getTokenInfo());
        Stepiks currentUserInfo = stepikApiClient.stepiks()
                .get()
                .id(1)
                .execute();
        currentUser.update(currentUserInfo.getUser());
        return currentUser;
    }

    public static StepikApiClient getStepikApiClient() {
        return stepikApiClient;
    }
}