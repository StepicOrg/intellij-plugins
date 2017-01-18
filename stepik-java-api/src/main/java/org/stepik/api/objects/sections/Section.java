package org.stepik.api.objects.sections;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class Section extends AbstractObject {
    private int course;
    private List<Long> units;
    private int position;
    @SerializedName("discounting_policy")
    private String discountingPolicy;
    private String progress;
    private Map<String, String> actions;
    @SerializedName("required_section")
    private String requiredSection;
    @SerializedName("required_percent")
    private int requiredPercent;
    @SerializedName("is_requirement_satisfied")
    private boolean isRequirementSatisfied;
    @SerializedName("is_exam")
    private boolean isExam;
    @SerializedName("exam_duration_minutes")
    private int examDurationMinutes;
    @SerializedName("exam_session")
    private String examSession;
    @SerializedName("proctor_session")
    private String proctorSession;
    private String description;
    private String title;
    private String slug;
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

    @NotNull
    public List<Long> getUnits() {
        if (units == null) {
            units = new ArrayList<>();
        }
        return units;
    }

    public void setUnits(@Nullable List<Long> units) {
        this.units = units;
    }

    @NotNull
    public String getTitle() {
        if (title == null) {
            title = "";
        }
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    @Nullable
    public String getDiscountingPolicy() {
        return discountingPolicy;
    }

    public void setDiscountingPolicy(@Nullable String discountingPolicy) {
        this.discountingPolicy = discountingPolicy;
    }

    @Nullable
    public String getProgress() {
        return progress;
    }

    public void setProgress(@Nullable String progress) {
        this.progress = progress;
    }

    @Nullable
    public Map<String, String> getActions() {
        return actions;
    }

    public void setActions(@Nullable Map<String, String> actions) {
        this.actions = actions;
    }

    @Nullable
    public String getRequiredSection() {
        return requiredSection;
    }

    public void setRequiredSection(@Nullable String requiredSection) {
        this.requiredSection = requiredSection;
    }

    public int getRequiredPercent() {
        return requiredPercent;
    }

    public void setRequiredPercent(int requiredPercent) {
        this.requiredPercent = requiredPercent;
    }

    public boolean getIsRequirementSatisfied() {
        return isRequirementSatisfied;
    }

    public void setIsRequirementSatisfied(boolean isRequirementSatisfied) {
        this.isRequirementSatisfied = isRequirementSatisfied;
    }

    public boolean isExam() {
        return isExam;
    }

    public void setExam(boolean exam) {
        isExam = exam;
    }

    public int getExamDurationMinutes() {
        return examDurationMinutes;
    }

    public void setExamDurationMinutes(int examDurationMinutes) {
        this.examDurationMinutes = examDurationMinutes;
    }

    @Nullable
    public String getExamSession() {
        return examSession;
    }

    public void setExamSession(@Nullable String examSession) {
        this.examSession = examSession;
    }

    @Nullable
    public String getProctorSession() {
        return proctorSession;
    }

    public void setProctorSession(@Nullable String proctorSession) {
        this.proctorSession = proctorSession;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getSlug() {
        return slug;
    }

    public void setSlug(@Nullable String slug) {
        this.slug = slug;
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
