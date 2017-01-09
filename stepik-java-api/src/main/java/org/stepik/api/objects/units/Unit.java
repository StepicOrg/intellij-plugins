package org.stepik.api.objects.units;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Unit {
    private int id;
    private int section;
    private int lesson;
    private List<Integer> assignments;
    private int position;
    private String progress;
    @SerializedName("begin_date")
    private String beginDate;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("soft_deadline")
    private String softDeadline;
    @SerializedName("hard_deadline")
    private String hardDeadline;
    @SerializedName("grading_policy")
    private String gradingPolicy;
    @SerializedName("begin_date_source")
    private String beginDateSource;
    @SerializedName("end_date_source")
    private String endDateSource;
    @SerializedName("soft_deadline_source")
    private String softDeadlineSource;
    @SerializedName("hard_deadline_source")
    private String hardDeadlineSource;
    @SerializedName("grading_policy_source")
    private String gradingPolicySource;
    @SerializedName("is_active")
    private boolean isActive;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("update_date")
    private String updateDate;

    public int getLesson() {
        return lesson;
    }

    public void setLesson(int lesson) {
        this.lesson = lesson;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public List<Integer> getAssignments() {
        if (assignments == null) {
            assignments = new ArrayList<>();
        }
        return assignments;
    }

    public void setAssignments(@Nullable List<Integer> assignments) {
        this.assignments = assignments;
    }

    @Nullable
    public String getProgress() {
        return progress;
    }

    public void setProgress(@Nullable String progress) {
        this.progress = progress;
    }

    @Nullable
    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(@Nullable String beginDate) {
        this.beginDate = beginDate;
    }

    @Nullable
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(@Nullable String endDate) {
        this.endDate = endDate;
    }

    @Nullable
    public String getSoftDeadline() {
        return softDeadline;
    }

    public void setSoftDeadline(@Nullable String softDeadline) {
        this.softDeadline = softDeadline;
    }

    @Nullable
    public String getHardDeadline() {
        return hardDeadline;
    }

    public void setHardDeadline(@Nullable String hardDeadline) {
        this.hardDeadline = hardDeadline;
    }

    @Nullable
    public String getGradingPolicy() {
        return gradingPolicy;
    }

    public void setGradingPolicy(@Nullable String gradingPolicy) {
        this.gradingPolicy = gradingPolicy;
    }

    @Nullable
    public String getBeginDateSource() {
        return beginDateSource;
    }

    public void setBeginDateSource(@Nullable String beginDateSource) {
        this.beginDateSource = beginDateSource;
    }

    @Nullable
    public String getEndDateSource() {
        return endDateSource;
    }

    public void setEndDateSource(@Nullable String endDateSource) {
        this.endDateSource = endDateSource;
    }

    @Nullable
    public String getSoftDeadlineSource() {
        return softDeadlineSource;
    }

    public void setSoftDeadlineSource(@Nullable String softDeadlineSource) {
        this.softDeadlineSource = softDeadlineSource;
    }

    @Nullable
    public String getHardDeadlineSource() {
        return hardDeadlineSource;
    }

    public void setHardDeadlineSource(@Nullable String hardDeadlineSource) {
        this.hardDeadlineSource = hardDeadlineSource;
    }

    @Nullable
    public String getGradingPolicySource() {
        return gradingPolicySource;
    }

    public void setGradingPolicySource(@Nullable String gradingPolicySource) {
        this.gradingPolicySource = gradingPolicySource;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Nullable
    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(@Nullable String createDate) {
        this.createDate = createDate;
    }

    @Nullable
    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(@Nullable String updateDate) {
        this.updateDate = updateDate;
    }
}
