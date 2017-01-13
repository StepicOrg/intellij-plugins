package com.jetbrains.tmp.learning.stepik;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.auth.TokenInfo;

/**
 * @author meanmail
 */
class AuthInfo {
    private TokenInfo tokenInfo;
    private String password;
    private String username;

    @Nullable
    TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    void setTokenInfo(@Nullable TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    @NotNull
    String getPassword() {
        if (password == null) {
            password = "";
        }
        return password;
    }

    void setPassword(@Nullable String password) {
        this.password = password;
    }

    @NotNull
    String getUsername() {
        if (username == null) {
            username = "";
        }
        return username;
    }

    void setUsername(@Nullable String username) {
        this.username = username;
    }

    boolean valid() {
        return tokenInfo != null || (password != null && username != null);
    }
}
