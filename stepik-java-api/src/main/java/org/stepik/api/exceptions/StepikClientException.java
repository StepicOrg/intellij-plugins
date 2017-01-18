package org.stepik.api.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class StepikClientException extends RuntimeException {
    public StepikClientException(@Nullable String message) {
        super(message);
    }

    public StepikClientException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
