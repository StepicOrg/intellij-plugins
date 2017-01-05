package org.stepik.api.queries.submissions;

/**
 * @author meanmail
 */
public enum ReviewStatus {
    DONE, AWAITING;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
