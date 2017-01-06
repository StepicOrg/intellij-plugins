package org.stepik.api.objects.attempts;

/**
 * @author meanmail
 */
public class AttemptsPost {
    private AttemptPost attempt;

    public AttemptPost getAttempt() {
        if (attempt == null) {
            attempt = new AttemptPost();
        }
        return attempt;
    }
}
