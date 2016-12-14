package com.jetbrains.tmp.learning.stepik.entities;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author meanmail
 */
public class Submission {
    @Expose
    private int id;
    @Expose
    private String status;
    @Expose
    private Double score;
    @Expose
    private String hint;
    @Expose
    private String feedback;
    @Expose
    private String time;
    @Expose
    private Reply reply;
    @Expose
    private String reply_url;
    @Expose
    private int attempt;
    @Expose
    private String session;
    @Expose
    private double eta;

    Submission(String score, int attempt, ArrayList<SolutionFile> files) {
        reply = new Reply(files, score);
        this.attempt = attempt;
    }

    public Reply getReply() {
        return reply;
    }

    private final static SimpleDateFormat timeISOFormat = getTimeISOFormat();
    private final static SimpleDateFormat timeOutFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    @NotNull
    private static SimpleDateFormat getTimeISOFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        format.setTimeZone(tz);
        return format;
    }

    @Override
    public String toString() {
        String localTime;
        try {
            Date utcTime = timeISOFormat.parse(time);
            localTime = timeOutFormat.format(utcTime);
        } catch (ParseException e) {
            localTime = time;
        }

        return String.format("#%d %s %s", id, status, localTime);
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReply_url() {
        return reply_url;
    }

    public void setReply_url(String reply_url) {
        this.reply_url = reply_url;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public double getEta() {
        return eta;
    }

    public void setEta(double eta) {
        this.eta = eta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}