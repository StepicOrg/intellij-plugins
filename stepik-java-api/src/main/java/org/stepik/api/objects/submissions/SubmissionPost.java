package org.stepik.api.objects.submissions;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class SubmissionPost {
    private Reply reply;
    private long attempt;

    @NotNull
    public Reply getReply() {
        if (reply == null) {
            reply = new Reply();
        }
        return reply;
    }

    public long getAttempt() {
        return attempt;
    }

    public void setAttempt(long attempt) {
        this.attempt = attempt;
    }
}
