package org.stepik.api.objects.submissions;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author meanmail
 */
public class Submission extends AbstractObject {
    private final static SimpleDateFormat timeISOFormat = getTimeISOFormat();

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
    private transient Date utcTime;

    private static SimpleDateFormat getTimeISOFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        format.setTimeZone(tz);
        return format;
    }

    @NotNull
    public String getStatus() {
        if (status == null) {
            status = "";
        }
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

    @NotNull
    public Date getTime() {
        if (utcTime == null) {
            try {
                utcTime = timeISOFormat.parse(time);
            } catch (ParseException e) {
                return Date.from(Instant.EPOCH);
            }
        }
        return utcTime;
    }

    public void setTime(@Nullable Date time) {
        this.time = timeISOFormat.format(time);
        utcTime = time;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @NotNull
    public String getFeedback() {
        if (feedback == null) {
            feedback = "";
        }
        return feedback;
    }

    public void setFeedback(@Nullable String feedback) {
        this.feedback = feedback;
    }

    @NotNull
    public String getReplyUrl() {
        if (replyUrl == null) {
            replyUrl = "";
        }
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
