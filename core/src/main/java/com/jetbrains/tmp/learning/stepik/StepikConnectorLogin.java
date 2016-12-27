/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.tmp.learning.stepik;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.jetbrains.tmp.learning.StepikProjectManager;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StepikConnectorLogin {
    private static final Logger logger = Logger.getInstance(StepikConnectorLogin.class.getName());
    private static final String CLIENT_ID = "hUCWcq3hZHCmz0DKrDtwOWITLcYutzot7p4n59vU";
    private static CloseableHttpClient ourClient;
    private static StepikUser currentUser;

    // TODO sing_in
    @Nullable
    static CloseableHttpClient getHttpClient() {
        if (ourClient == null) {
            List<BasicHeader> headers = new ArrayList<>();
            if (currentUser != null && currentUser.getAccessToken() != null && !currentUser.getAccessToken()
                    .isEmpty()) {
                headers.add(new BasicHeader("Authorization", "Bearer " + currentUser.getAccessToken()));
                headers.add(new BasicHeader("Content-type", EduStepikNames.CONTENT_TYPE_APPL_JSON));
            } else {
                logger.warn("access_token is empty.. login..");
                showLoginDialog();
                headers.add(new BasicHeader("Authorization", "Bearer " + currentUser.getAccessToken()));
                headers.add(new BasicHeader("Content-type", EduStepikNames.CONTENT_TYPE_APPL_JSON));
            }
            HttpClientBuilder builder = StepikConnectorInit.getBuilder();
            if (builder != null) {
                ourClient = builder.setDefaultHeaders(headers).build();
            }
        }
        return ourClient;
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
        ourClient = null;
        currentUser = null;
    }

    @Nullable
    static StepikUser minorLogin(@NotNull StepikUser basicUser) {
        String refreshToken;
        StepikWrappers.TokenInfo tokenInfo = null;
        List<NameValuePair> nvps = new ArrayList<>();

        if (!(refreshToken = basicUser.getRefreshToken()).isEmpty()) {
            logger.info("refresh_token auth");
            nvps.add(new BasicNameValuePair("client_id", CLIENT_ID));
            nvps.add(new BasicNameValuePair("content-type", "application/json"));
            nvps.add(new BasicNameValuePair("grant_type", "refresh_token"));
            nvps.add(new BasicNameValuePair("refresh_token", refreshToken));

            tokenInfo = postCredentials(nvps);
        }

        nvps.clear();

        if (tokenInfo == null) {
            logger.info("credentials auth");
            String password = basicUser.getPassword();
            if (password.isEmpty()) return null;
            nvps.add(new BasicNameValuePair("client_id", CLIENT_ID));
            nvps.add(new BasicNameValuePair("grant_type", "password"));
            nvps.add(new BasicNameValuePair("username", basicUser.getEmail()));
            nvps.add(new BasicNameValuePair("password", password));

            tokenInfo = postCredentials(nvps);
        }

        if (tokenInfo == null) {
            return null;
        }
        currentUser = new StepikUser(basicUser);
        currentUser.setupTokenInfo(tokenInfo);
        StepikWrappers.AuthorWrapper userWrapper = StepikConnectorGet.getCurrentUser();
        if (userWrapper != null) {
            currentUser.update(userWrapper.users.get(0));
        } else {
            return null;
        }
        return currentUser;
    }

    private static StepikWrappers.TokenInfo postCredentials(@NotNull List<NameValuePair> nvps) {
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        final HttpPost request = new HttpPost(EduStepikNames.TOKEN_URL);
        request.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

        try {
            CloseableHttpClient client = StepikConnectorInit.getHttpClient();
            if (client == null) {
                logger.warn("Failed to Login: httpClient is null");
                return null;
            }
            final CloseableHttpResponse response = client.execute(request);
            final StatusLine statusLine = response.getStatusLine();
            final HttpEntity responseEntity = response.getEntity();
            final String responseString = responseEntity != null ? EntityUtils.toString(responseEntity) : "";
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                return gson.fromJson(responseString, StepikWrappers.TokenInfo.class);
            } else {
                logger.warn("Failed to Login: " + statusLine.getStatusCode() + statusLine.getReasonPhrase());
                throw new IOException("Stepik returned non 200 status code " + responseString);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }
}