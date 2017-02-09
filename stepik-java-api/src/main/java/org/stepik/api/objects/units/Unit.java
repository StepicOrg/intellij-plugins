package org.stepik.api.objects.units;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Unit extends AbstractObject {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Unit unit = (Unit) o;

        if (section != unit.section) return false;
        if (lesson != unit.lesson) return false;
        if (position != unit.position) return false;
        if (isActive != unit.isActive) return false;
        if (assignments != null ? !assignments.equals(unit.assignments) : unit.assignments != null) return false;
        if (progress != null ? !progress.equals(unit.progress) : unit.progress != null) return false;
        if (beginDate != null ? !beginDate.equals(unit.beginDate) : unit.beginDate != null) return false;
        if (endDate != null ? !endDate.equals(unit.endDate) : unit.endDate != null) return false;
        if (softDeadline != null ? !softDeadline.equals(unit.softDeadline) : unit.softDeadline != null) return false;
        if (hardDeadline != null ? !hardDeadline.equals(unit.hardDeadline) : unit.hardDeadline != null) return false;
        if (gradingPolicy != null ? !gradingPolicy.equals(unit.gradingPolicy) : unit.gradingPolicy != null)
            return false;
        if (beginDateSource != null ? !beginDateSource.equals(unit.beginDateSource) : unit.beginDateSource != null)
            return false;
        if (endDateSource != null ? !endDateSource.equals(unit.endDateSource) : unit.endDateSource != null)
            return false;
        if (softDeadlineSource != null ?
                !softDeadlineSource.equals(unit.softDeadlineSource) :
                unit.softDeadlineSource != null) return false;
        if (hardDeadlineSource != null ?
                !hardDeadlineSource.equals(unit.hardDeadlineSource) :
                unit.hardDeadlineSource != null) return false;
        if (gradingPolicySource != null ?
                !gradingPolicySource.equals(unit.gradingPolicySource) :
                unit.gradingPolicySource != null) return false;
        //noinspection SimplifiableIfStatement
        if (createDate != null ? !createDate.equals(unit.createDate) : unit.createDate != null) return false;
        return updateDate != null ? updateDate.equals(unit.updateDate) : unit.updateDate == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + section;
        result = 31 * result + lesson;
        result = 31 * result + (assignments != null ? assignments.hashCode() : 0);
        result = 31 * result + position;
        result = 31 * result + (progress != null ? progress.hashCode() : 0);
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
