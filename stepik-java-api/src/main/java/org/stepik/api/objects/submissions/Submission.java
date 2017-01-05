package org.stepik.api.objects.submissions;

import com.google.gson.annotations.SerializedName;

/**
 * @author meanmail
 */
public class Submission {
    private int id;
    private String status;
    private Double score;
    private String hint;
    private String feedback;
    private String time;
    private Reply reply;
    @SerializedName("reply_url")
    private String replyUrl;
    private int attempt;
    private String session;
    private int eta;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getHint() {
        return hint;
    }

    public Reply getReply() {
        return reply;
    }
}
