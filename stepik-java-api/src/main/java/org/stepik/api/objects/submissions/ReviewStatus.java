package org.stepik.api.objects.submissions;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public enum ReviewStatus {
    DONE, AWAITING;

    @NotNull
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
