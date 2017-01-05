package org.stepik.api.auth;

/**
 * @author meanmail
 */
public enum GrantTypes {
    REFRESH_TOKEN, PASSWORD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
