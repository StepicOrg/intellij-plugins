package org.stepik.api.objects.submissions;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

/**
 * @author meanmail
 */
public class Submission extends AbstractObject {
    private String status;
    private double score;
    private String hint;
    private String feedback;
    private String time;
    private Reply reply;
    @SerializedName("reply_url")
    private String replyUrl;
    private int attempt;
    private String session;
    private double eta;

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    @NotNull
    public String getHint() {
        if (hint == null) {
            hint = "";
        }
        return hint;
    }

    public void setHint(@Nullable String hint) {
        this.hint = hint;
    }

    @NotNull
    public Reply getReply() {
        if (reply == null) {
            reply = new Reply();
        }
        return reply;
    }

    public void setReply(@Nullable Reply reply) {
        this.reply = reply;
    }

    @Nullable
    public String getTime() {
        return time;
    }

    public void setTime(@Nullable String time) {
        this.time = time;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Nullable
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(@Nullable String feedback) {
        this.feedback = feedback;
    }

    @Nullable
    public String getReplyUrl() {
        return replyUrl;
    }

    public void setReplyUrl(@Nullable String replyUrl) {
        this.replyUrl = replyUrl;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    @Nullable
    public String getSession() {
        return session;
    }

    public void setSession(@Nullable String session) {
        this.session = session;
    }

    public double getEta() {
        return eta;
    }

    public void setEta(double eta) {
        this.eta = eta;
    }
}
