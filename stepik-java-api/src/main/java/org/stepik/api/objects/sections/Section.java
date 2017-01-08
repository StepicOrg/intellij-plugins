package org.stepik.api.objects.sections;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class Section {
    private int id;
    private int course;
    private List<Integer> units;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getUnits() {
        if (units == null) {
            units = new ArrayList<>();
        }
        return units;
    }

    public void setUnits(List<Integer> units) {
        this.units = units;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
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

    public String getDiscountingPolicy() {
        return discountingPolicy;
    }

    public void setDiscountingPolicy(String discountingPolicy) {
        this.discountingPolicy = discountingPolicy;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Map<String, String> getActions() {
        return actions;
    }

    public void setActions(Map<String, String> actions) {
        this.actions = actions;
    }

    public String getRequiredSection() {
        return requiredSection;
    }

    public void setRequiredSection(String requiredSection) {
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

    public String getExamSession() {
        return examSession;
    }

    public void setExamSession(String examSession) {
        this.examSession = examSession;
    }

    public String getProctorSession() {
        return proctorSession;
    }

    public void setProctorSession(String proctorSession) {
        this.proctorSession = proctorSession;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSoftDeadline() {
        return softDeadline;
    }

    public void setSoftDeadline(String softDeadline) {
        this.softDeadline = softDeadline;
    }

    public String getHardDeadline() {
        return hardDeadline;
    }

    public void setHardDeadline(String hardDeadline) {
        this.hardDeadline = hardDeadline;
    }

    public String getGradingPolicy() {
        return gradingPolicy;
    }

    public void setGradingPolicy(String gradingPolicy) {
        this.gradingPolicy = gradingPolicy;
    }

    public String getBeginDateSource() {
        return beginDateSource;
    }

    public void setBeginDateSource(String beginDateSource) {
        this.beginDateSource = beginDateSource;
    }

    public String getEndDateSource() {
        return endDateSource;
    }

    public void setEndDateSource(String endDateSource) {
        this.endDateSource = endDateSource;
    }

    public String getSoftDeadlineSource() {
        return softDeadlineSource;
    }

    public void setSoftDeadlineSource(String softDeadlineSource) {
        this.softDeadlineSource = softDeadlineSource;
    }

    public String getHardDeadlineSource() {
        return hardDeadlineSource;
    }

    public void setHardDeadlineSource(String hardDeadlineSource) {
        this.hardDeadlineSource = hardDeadlineSource;
    }

    public String getGradingPolicySource() {
        return gradingPolicySource;
    }

    public void setGradingPolicySource(String gradingPolicySource) {
        this.gradingPolicySource = gradingPolicySource;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
