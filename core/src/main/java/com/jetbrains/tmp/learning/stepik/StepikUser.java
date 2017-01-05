package com.jetbrains.tmp.learning.stepik;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.StepikProjectManager;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.auth.TokenInfo;
import org.stepik.api.objects.users.User;

public class StepikUser {
    private int id;
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String accessToken = "";
    private String refreshToken = "";

    public StepikUser() {
    }

    public StepikUser(@NotNull final String email, @NotNull final String password) {
        this.email = email;
        setPassword(password);
    }

    StepikUser(StepikUser basicUser) {
        this.email = basicUser.getEmail();
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public String getFirstName() {
        return firstName;
    }

    @SuppressWarnings("unused")
    public void setFirstName(@NotNull final String firstName) {
        this.firstName = firstName;
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public String getLastName() {
        return lastName;
    }

    @SuppressWarnings("unused")
    public void setLastName(@NotNull final String last_name) {
        this.lastName = last_name;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public void setEmail(@NotNull final String email) {
        this.email = email;
    }

    @Transient
    @NotNull
    public String getPassword() {
        final String email = getEmail();
        if (StringUtil.isEmptyOrSpaces(email)) return "";

        String serviceName = StepikProjectManager.class.getName();
        CredentialAttributes attributes = new CredentialAttributes(serviceName,
                email,
                StepikProjectManager.class,
                false);
        String password = PasswordSafe.getInstance().getPassword(attributes);

        return StringUtil.notNullize(password);
    }

    @Transient
    private void setPassword(@NotNull final String password) {
        if (password.isEmpty()) return;

        String serviceName = StepikProjectManager.class.getName();
        CredentialAttributes attributes = new CredentialAttributes(serviceName,
                email,
                StepikProjectManager.class,
                false);
        PasswordSafe.getInstance().setPassword(attributes, password);
    }

    @NotNull
    public String getName() {
        return StringUtil.join(new String[]{firstName, lastName}, " ");
    }

    public String getAccessToken() {
        return accessToken;
    }

    @SuppressWarnings("unused")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @SuppressWarnings("unused")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    void setupTokenInfo(TokenInfo tokenInfo) {
        accessToken = tokenInfo.getAccessToken();
        refreshToken = tokenInfo.getRefreshToken();
    }

    public void update(User tmpUser) {
        id = tmpUser.getId();
        firstName = tmpUser.getFirstName();
        lastName = tmpUser.getLastName();
    }

    @Override
    public String toString() {
        return "StepikUser{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public static StepikUser fromUser(User user) {
        StepikUser result = new StepikUser();

        result.setId(user.getId());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());

        return result;
    }
}
