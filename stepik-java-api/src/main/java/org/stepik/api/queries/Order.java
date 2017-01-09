package org.stepik.api.queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public enum Order {
    ASC, DESC;

    @NotNull
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
