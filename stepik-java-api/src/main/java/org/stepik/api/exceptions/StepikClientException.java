package org.stepik.api.exceptions;

/**
 * @author meanmail
 */
public class StepikClientException extends RuntimeException {
    public StepikClientException(String message) {
        super(message);
    }

    public StepikClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
