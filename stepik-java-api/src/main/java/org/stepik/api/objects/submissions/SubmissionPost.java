package org.stepik.api.objects.submissions;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class SubmissionPost {
    private Reply reply;
    private int attempt;

    @NotNull
    public Reply getReply() {
        if (reply == null) {
            reply = new Reply();
        }
        return reply;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }
}
