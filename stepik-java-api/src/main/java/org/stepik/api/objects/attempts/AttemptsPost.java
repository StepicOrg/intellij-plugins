package org.stepik.api.objects.attempts;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class AttemptsPost {
    private AttemptPost attempt;

    @NotNull
    public AttemptPost getAttempt() {
        if (attempt == null) {
            attempt = new AttemptPost();
        }
        return attempt;
    }
}
