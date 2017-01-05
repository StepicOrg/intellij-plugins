package org.stepik.api.objects.steps;

import com.google.gson.annotations.SerializedName;

/**
 * @author meanmail
 */
public class Step {
    private int id;
    private int lesson;
    private int position;
    private String status;
    private BlockView block;
    private Object actions;
    private String progress;
    private String[] subscriptions;
    private String instruction;
    private String session;
    @SerializedName("instruction_type")
    private String instructionType;
    @SerializedName("viewed_by")
    private int viewedBy;
    @SerializedName("passed_by")
    private int passedBy;
    @SerializedName("correct_ratio")
    private String correctRatio;
    private String worth;
    @SerializedName("is_solutions_unlocked")
    private boolean isSolutionsUnlocked;
    @SerializedName("solutions_unlocked_attempts")
    private String solutionsUnlockedAttempts;
    @SerializedName("has_submissions_restrictions")
    private boolean hasSubmissionsRestrictions;
    @SerializedName("max_submissions_count")
    private int maxSubmissionsCount;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("update_date")
    private String updateDate;
    @SerializedName("discussions_count")
    private int discussionsCount;
    @SerializedName("discussion_proxy")
    private String discussionProxy;
    @SerializedName("discussion_threads")
    private String[] discussionThreads;

    public BlockView getBlock() {
        return block;
    }

    public int getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public String getStatus() {
        return status;
    }
}
