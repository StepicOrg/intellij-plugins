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
package org.stepic.plugin.java.ui;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.ide.passwordSafe.config.PasswordSafeSettings;
import com.intellij.ide.passwordSafe.impl.PasswordSafeImpl;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.stepic.plugin.java.ui.GithubAuthData.AuthType;

@SuppressWarnings("MethodMayBeStatic")
@State(name = "StepicSettings", storages = @Storage("stepic_settings.xml"))
public class StepicSettings implements PersistentStateComponent<StepicSettings.State> {
  private static final Logger LOG = Logger.getInstance(StepicSettings.class.getName());
  private static final String STEPIC_SETTINGS_PASSWORD_KEY = "STEPIC_SETTINGS_PASSWORD_KEY";

  private State myState = new State();

  public State getState() {
    return myState;
  }

  public void loadState(State state) {
    myState = state;
  }

  public static class State {
    @Nullable public String LOGIN = null;
    @NotNull public AuthType AUTH_TYPE = AuthType.ANONYMOUS;
    public boolean ANONYMOUS_GIST = false;
    public boolean OPEN_IN_BROWSER_GIST = true;
    public boolean PRIVATE_GIST = true;
    public boolean SAVE_PASSWORD = true;
    public boolean VALID_GIT_AUTH = true;
    public ThreeState CREATE_PULL_REQUEST_CREATE_REMOTE = ThreeState.UNSURE;
    public boolean CLONE_GIT_USING_SSH = false;
  }

  public static StepicSettings getInstance() {
    return ServiceManager.getService(StepicSettings.class);
  }

  @Nullable
  public String getLogin() {
    return myState.LOGIN;
  }

  @NotNull
  public AuthType getAuthType() {
    return myState.AUTH_TYPE;
  }

  public boolean isAuthConfigured() {
    return !myState.AUTH_TYPE.equals(AuthType.ANONYMOUS);
  }

  private void setLogin(@Nullable String login) {
    myState.LOGIN = login;
  }

  private void setAuthType(@NotNull AuthType authType) {
    myState.AUTH_TYPE = authType;
  }

  public boolean isAnonymousGist() {
    return myState.ANONYMOUS_GIST;
  }

  public boolean isOpenInBrowserGist() {
    return myState.OPEN_IN_BROWSER_GIST;
  }

  public boolean isPrivateGist() {
    return myState.PRIVATE_GIST;
  }

  public boolean isSavePassword() {
    return myState.SAVE_PASSWORD;
  }

  public boolean isValidGitAuth() {
    return myState.VALID_GIT_AUTH;
  }

  public boolean isSavePasswordMakesSense() {
    final PasswordSafeImpl passwordSafe = (PasswordSafeImpl)PasswordSafe.getInstance();
    return passwordSafe.getSettings().getProviderType() == PasswordSafeSettings.ProviderType.MASTER_PASSWORD;
  }

  public boolean isCloneGitUsingSsh() {
    return myState.CLONE_GIT_USING_SSH;
  }

  @NotNull
  public ThreeState getCreatePullRequestCreateRemote() {
    return myState.CREATE_PULL_REQUEST_CREATE_REMOTE;
  }

  public void setCreatePullRequestCreateRemote(@NotNull ThreeState value) {
    myState.CREATE_PULL_REQUEST_CREATE_REMOTE = value;
  }

  public void setAnonymousGist(final boolean anonymousGist) {
    myState.ANONYMOUS_GIST = anonymousGist;
  }

  public void setPrivateGist(final boolean privateGist) {
    myState.PRIVATE_GIST = privateGist;
  }

  public void setSavePassword(final boolean savePassword) {
    myState.SAVE_PASSWORD = savePassword;
  }

  public void setValidGitAuth(final boolean validGitAuth) {
    myState.VALID_GIT_AUTH = validGitAuth;
  }

  public void setOpenInBrowserGist(final boolean openInBrowserGist) {
    myState.OPEN_IN_BROWSER_GIST = openInBrowserGist;
  }

  public void setCloneGitUsingSsh(boolean value) {
    myState.CLONE_GIT_USING_SSH = value;
  }

  @NotNull
  private String getPassword() {
    String password;
    try {
      password = PasswordSafe.getInstance().getPassword(null, StepicSettings.class, STEPIC_SETTINGS_PASSWORD_KEY);
    }
    catch (PasswordSafeException e) {
      LOG.info("Couldn't get password for key [" + STEPIC_SETTINGS_PASSWORD_KEY + "]", e);
      password = "";
    }

    return StringUtil.notNullize(password);
  }

  private void setPassword(@NotNull String password, boolean rememberPassword) {
    try {
      if (rememberPassword) {
        PasswordSafe.getInstance().storePassword(null, StepicSettings.class, STEPIC_SETTINGS_PASSWORD_KEY, password);
      }
      else {
        final PasswordSafeImpl passwordSafe = (PasswordSafeImpl)PasswordSafe.getInstance();
        if (passwordSafe.getSettings().getProviderType() != PasswordSafeSettings.ProviderType.DO_NOT_STORE) {
          passwordSafe.getMemoryProvider().storePassword(null, StepicSettings.class, STEPIC_SETTINGS_PASSWORD_KEY, password);
        }
      }
    }
    catch (PasswordSafeException e) {
      LOG.info("Couldn't set password for key [" + STEPIC_SETTINGS_PASSWORD_KEY + "]", e);
    }
  }

  private static boolean isValidGitAuth(@NotNull GithubAuthData auth) {
    switch (auth.getAuthType()) {
      case BASIC:
        assert auth.getBasicAuth() != null;
        return auth.getBasicAuth().getCode() == null;
      case TOKEN:
        return true;
      case ANONYMOUS:
        return false;
      default:
        throw new IllegalStateException("StepicSettings: setAuthData - wrong AuthType: " + auth.getAuthType());
    }
  }

  @NotNull
  public GithubAuthData getAuthData() {
    switch (getAuthType()) {
      case BASIC:
        //noinspection ConstantConditions
        return GithubAuthData.createBasicAuth(getLogin(), getPassword());
      case TOKEN:
        return GithubAuthData.createTokenAuth(getPassword());
      case ANONYMOUS:
        return GithubAuthData.createAnonymous();
      default:
        throw new IllegalStateException("StepicSettings: getAuthData - wrong AuthType: " + getAuthType());
    }
  }

  public void setAuthData(@NotNull GithubAuthData auth, boolean rememberPassword) {
    setValidGitAuth(isValidGitAuth(auth));

    setAuthType(auth.getAuthType());

    switch (auth.getAuthType()) {
      case BASIC:
        assert auth.getBasicAuth() != null;
        setLogin(auth.getBasicAuth().getLogin());
        setPassword(auth.getBasicAuth().getPassword(), rememberPassword);
        break;
      case TOKEN:
        assert auth.getTokenAuth() != null;
        setLogin(null);
        setPassword(auth.getTokenAuth().getToken(), rememberPassword);
        break;
      case ANONYMOUS:
        setLogin(null);
        setPassword("", rememberPassword);
        break;
      default:
        throw new IllegalStateException("StepicSettings: setAuthData - wrong AuthType: " + auth.getAuthType());
    }
  }
}