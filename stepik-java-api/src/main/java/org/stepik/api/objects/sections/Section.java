package org.stepik.api.objects.sections;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.Utils;
import org.stepik.api.objects.StudyObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author meanmail
 */
public class Section extends StudyObject {
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
    private transient Date utcUpdateDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Section section = (Section) o;

        if (course != section.course) return false;
        if (position != section.position) return false;
        if (requiredPercent != section.requiredPercent) return false;
        if (isRequirementSatisfied != section.isRequirementSatisfied) return false;
        if (isExam != section.isExam) return false;
        if (examDurationMinutes != section.examDurationMinutes) return false;
        if (isActive != section.isActive) return false;
        if (units != null ? !units.equals(section.units) : section.units != null) return false;
        if (discountingPolicy != null ?
                !discountingPolicy.equals(section.discountingPolicy) :
                section.discountingPolicy != null) return false;
        if (progress != null ? !progress.equals(section.progress) : section.progress != null) return false;
        if (actions != null ? !actions.equals(section.actions) : section.actions != null) return false;
        if (requiredSection != null ?
                !requiredSection.equals(section.requiredSection) :
                section.requiredSection != null)
            return false;
        if (examSession != null ? !examSession.equals(section.examSession) : section.examSession != null) return false;
        if (proctorSession != null ? !proctorSession.equals(section.proctorSession) : section.proctorSession != null)
            return false;
        if (description != null ? !description.equals(section.description) : section.description != null) return false;
        if (title != null ? !title.equals(section.title) : section.title != null) return false;
        if (slug != null ? !slug.equals(section.slug) : section.slug != null) return false;
        if (beginDate != null ? !beginDate.equals(section.beginDate) : section.beginDate != null) return false;
        if (endDate != null ? !endDate.equals(section.endDate) : section.endDate != null) return false;
        if (softDeadline != null ? !softDeadline.equals(section.softDeadline) : section.softDeadline != null)
            return false;
        if (hardDeadline != null ? !hardDeadline.equals(section.hardDeadline) : section.hardDeadline != null)
            return false;
        if (gradingPolicy != null ? !gradingPolicy.equals(section.gradingPolicy) : section.gradingPolicy != null)
            return false;
        if (beginDateSource != null ?
                !beginDateSource.equals(section.beginDateSource) :
                section.beginDateSource != null)
            return false;
        if (endDateSource != null ? !endDateSource.equals(section.endDateSource) : section.endDateSource != null)
            return false;
        if (softDeadlineSource != null ?
                !softDeadlineSource.equals(section.softDeadlineSource) :
                section.softDeadlineSource != null) return false;
        if (hardDeadlineSource != null ?
                !hardDeadlineSource.equals(section.hardDeadlineSource) :
                section.hardDeadlineSource != null) return false;
        if (gradingPolicySource != null ?
                !gradingPolicySource.equals(section.gradingPolicySource) :
                section.gradingPolicySource != null) return false;
        //noinspection SimplifiableIfStatement
        if (createDate != null ? !createDate.equals(section.createDate) : section.createDate != null) return false;
        return updateDate != null ? updateDate.equals(section.updateDate) : section.updateDate == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + course;
        result = 31 * result + (units != null ? units.hashCode() : 0);
        result = 31 * result + position;
        result = 31 * result + (discountingPolicy != null ? discountingPolicy.hashCode() : 0);
        result = 31 * result + (progress != null ? progress.hashCode() : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (requiredSection != null ? requiredSection.hashCode() : 0);
        result = 31 * result + requiredPercent;
        result = 31 * result + (isRequirementSatisfied ? 1 : 0);
        result = 31 * result + (isExam ? 1 : 0);
        result = 31 * result + examDurationMinutes;
        result = 31 * result + (examSession != null ? examSession.hashCode() : 0);
        result = 31 * result + (proctorSession != null ? proctorSession.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (slug != null ? slug.hashCode() : 0);
        result = 31 * result + (beginDate != null ? beginDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (softDeadline != null ? softDeadline.hashCode() : 0);
        result = 31 * result + (hardDeadline != null ? hardDeadline.hashCode() : 0);
        result = 31 * result + (gradingPolicy != null ? gradingPolicy.hashCode() : 0);
        result = 31 * result + (beginDateSource != null ? beginDateSource.hashCode() : 0);
        result = 31 * result + (endDateSource != null ? endDateSource.hashCode() : 0);
        result = 31 * result + (softDeadlineSource != null ? softDeadlineSource.hashCode() : 0);
        result = 31 * result + (hardDeadlineSource != null ? hardDeadlineSource.hashCode() : 0);
        result = 31 * result + (gradingPolicySource != null ? gradingPolicySource.hashCode() : 0);
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        return result;
    }

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

    @NotNull
    public String getDescription() {
        if (description == null) {
            description = "";
        }
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

    @NotNull
    public Date getUpdateDate() {
        if (utcUpdateDate == null) {
            utcUpdateDate = Utils.INSTANCE.toDate(updateDate);
        }
        return utcUpdateDate;
    }

    public void setUpdateDate(@Nullable Date updateDate) {
        this.updateDate = Utils.INSTANCE.getTimeISOFormat().format(updateDate);
        utcUpdateDate = updateDate;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
