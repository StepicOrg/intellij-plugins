package org.stepik.api.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class StepikUnauthorizedException extends StepikClientException {
    public StepikUnauthorizedException(@Nullable String message) {
        super(message);
    }
}
