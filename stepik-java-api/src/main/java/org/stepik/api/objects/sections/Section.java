package org.stepik.api.objects.sections;

import com.google.gson.annotations.SerializedName;

/**
 * @author meanmail
 */
public class Section {
    private int id;
    private int course;
    private int[] units;
    private int position;
    @SerializedName("discounting_policy")
    private String discountingPolicy;
    private String progress;
    private Object actions;
    @SerializedName("required_section")
    private String requiredSection;
    @SerializedName("required_percent")
    private int requiredPercent;
    @SerializedName("is_requirement_satisfied")
    private String isRequirementSatisfied;
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
}
