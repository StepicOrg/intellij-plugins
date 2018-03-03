package org.stepik.api.auth;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public enum GrantTypes {
    REFRESH_TOKEN, PASSWORD, AUTHORIZATION_CODE;

    @NotNull
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
