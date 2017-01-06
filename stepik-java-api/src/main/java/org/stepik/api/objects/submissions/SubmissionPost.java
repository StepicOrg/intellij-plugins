package org.stepik.api.objects.submissions;

/**
 * @author meanmail
 */
public class SubmissionPost {
    private Reply reply;
    private int attempt;

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
