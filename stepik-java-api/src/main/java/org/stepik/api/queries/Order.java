package org.stepik.api.queries;

/**
 * @author meanmail
 */
public enum Order {
    ASC, DESC;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
