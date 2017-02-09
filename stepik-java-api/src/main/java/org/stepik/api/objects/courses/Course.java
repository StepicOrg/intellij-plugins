package org.stepik.api.objects.courses;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;
import org.stepik.api.objects.steps.Video;
import org.stepik.api.urls.Urls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class Course extends AbstractObject {
    private String summary;
    private String workload;
    private String cover;
    private String intro;
    @SerializedName("course_format")
    private String courseFormat;
    @SerializedName("target_audience")
    private String targetAudience;
    @SerializedName("certificate_footer")
    private String certificateFooter;
    @SerializedName("certificate_cover_org")
    private String certificateCoverOrg;
    @SerializedName("is_certificate_auto_issued")
    private boolean isCertificateAutoIssued;
    @SerializedName("certificate_regular_threshold")
    private int certificateRegularThreshold;
    @SerializedName("certificate_distinction_threshold")
    private int certificateDistinctionThreshold;
    private List<Integer> instructors;
    private String certificate;
    private String requirements;
    private String description;
    private List<Long> sections;
    @SerializedName("total_units")
    private int totalUnits;
    private int enrollment;
    @SerializedName("is_favorite")
    private boolean isFavorite;
    private Map<String, String> actions;
    private String progress;
    @SerializedName("certificate_link")
    private String certificateLink;
    @SerializedName("certificate_regular_link")
    private String certificateRegularLink;
    @SerializedName("certificate_distinction_link")
    private String certificateDistinctionLink;
    @SerializedName("schedule_link")
    private String scheduleLink;
    @SerializedName("schedule_long_link")
    private String scheduleLongLink;
    @SerializedName("first_deadline")
    private String firstDeadline;
    @SerializedName("last_deadline")
    private String lastDeadline;
    private List<String> subscriptions;
    private List<Integer> announcements;
    @SerializedName("is_contest")
    private boolean isContest;
    @SerializedName("is_self_paced")
    private boolean isSelfPaced;
    @SerializedName("is_adaptive")
    private boolean isAdaptive;
    @SerializedName("is_idea_compatible")
    private boolean isIdeaCompatible;
    @SerializedName("last_step")
    private String lastStep;
    @SerializedName("intro_video")
    private Video introVideo;
    @SerializedName("social_providers")
    private List<String> socialProviders;
    private List<Long> authors;
    private List<Integer> tags;
    @SerializedName("has_tutors")
    private boolean hasTutors;
    @SerializedName("is_enabled")
    private boolean isEnabled;
    @SerializedName("is_proctored")
    private boolean isProctored;
    @SerializedName("review_summary")
    private int reviewSummary;
    @SerializedName("certificates_count")
    private int certificatesCount;
    @SerializedName("learners_count")
    private int learnersCount;
    private int owner;
    private String language;
    @SerializedName("is_featured")
    private boolean isFeatured;
    @SerializedName("is_public")
    private boolean isPublic;
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
    @SerializedName("learners_group")
    private String learnersGroup;
    @SerializedName("testers_group")
    private String testersGroup;
    @SerializedName("moderators_group")
    private String moderatorsGroup;
    @SerializedName("teachers_group")
    private String teachersGroup;
    @SerializedName("admins_group")
    private String adminsGroup;
    @SerializedName("discussions_count")
    private int discussionsCount;
    @SerializedName("discussion_proxy")
    private String discussionProxy;
    @SerializedName("discussion_threads")
    private List<String> discussionThreads;

    public boolean isAdaptive() {
        return isAdaptive;
    }

    public void setAdaptive(boolean adaptive) {
        isAdaptive = adaptive;
    }

    @NotNull
    public List<Long> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public void setAuthors(@Nullable List<Long> authors) {
        this.authors = authors;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
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

    @NotNull
    public List<Long> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    public void setSections(@Nullable List<Long> sections) {
        this.sections = sections;
    }

    @Nullable
    @Override
    public String toString() {
        return title;
    }

    @Nullable
    public String getSummary() {
        return summary;
    }

    public void setSummary(@Nullable String summary) {
        this.summary = summary;
    }

    @Nullable
    public String getWorkload() {
        return workload;
    }

    public void setWorkload(@Nullable String workload) {
        this.workload = workload;
    }

    @Nullable
    public String getCover() {
        return cover;
    }

    public void setCover(@Nullable String cover) {
        this.cover = cover;
    }

    @Nullable
    public String getIntro() {
        return intro;
    }

    public void setIntro(@Nullable String intro) {
        this.intro = intro;
    }

    @Nullable
    public String getCourseFormat() {
        return courseFormat;
    }

    public void setCourseFormat(@Nullable String courseFormat) {
        this.courseFormat = courseFormat;
    }

    @Nullable
    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(@Nullable String targetAudience) {
        this.targetAudience = targetAudience;
    }

    @Nullable
    public String getCertificateFooter() {
        return certificateFooter;
    }

    public void setCertificateFooter(@Nullable String certificateFooter) {
        this.certificateFooter = certificateFooter;
    }

    @Nullable
    public String getCertificateCoverOrg() {
        return certificateCoverOrg;
    }

    public void setCertificateCoverOrg(@Nullable String certificateCoverOrg) {
        this.certificateCoverOrg = certificateCoverOrg;
    }

    public boolean isCertificateAutoIssued() {
        return isCertificateAutoIssued;
    }

    public void setCertificateAutoIssued(boolean certificateAutoIssued) {
        isCertificateAutoIssued = certificateAutoIssued;
    }

    public int getCertificateRegularThreshold() {
        return certificateRegularThreshold;
    }

    public void setCertificateRegularThreshold(int certificateRegularThreshold) {
        this.certificateRegularThreshold = certificateRegularThreshold;
    }

    public int getCertificateDistinctionThreshold() {
        return certificateDistinctionThreshold;
    }

    public void setCertificateDistinctionThreshold(int certificateDistinctionThreshold) {
        this.certificateDistinctionThreshold = certificateDistinctionThreshold;
    }

    @NotNull
    public List<Integer> getInstructors() {
        if (instructors == null) {
            instructors = new ArrayList<>();
        }
        return instructors;
    }

    public void setInstructors(@Nullable List<Integer> instructors) {
        this.instructors = instructors;
    }

    @Nullable
    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(@Nullable String certificate) {
        this.certificate = certificate;
    }

    @Nullable
    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(@Nullable String requirements) {
        this.requirements = requirements;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    public int getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(int enrollment) {
        this.enrollment = enrollment;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Nullable
    public Map<String, String> getActions() {
        return actions;
    }

    public void setActions(@Nullable Map<String, String> actions) {
        this.actions = actions;
    }

    @Nullable
    public String getProgress() {
        return progress;
    }

    public void setProgress(@Nullable String progress) {
        this.progress = progress;
    }

    @Nullable
    public String getCertificateLink() {
        return certificateLink;
    }

    public void setCertificateLink(@Nullable String certificateLink) {
        this.certificateLink = certificateLink;
    }

    @Nullable
    public String getCertificateRegularLink() {
        return certificateRegularLink;
    }

    public void setCertificateRegularLink(@Nullable String certificateRegularLink) {
        this.certificateRegularLink = certificateRegularLink;
    }

    @Nullable
    public String getCertificateDistinctionLink() {
        return certificateDistinctionLink;
    }

    public void setCertificateDistinctionLink(@Nullable String certificateDistinctionLink) {
        this.certificateDistinctionLink = certificateDistinctionLink;
    }

    @Nullable
    public String getScheduleLink() {
        return scheduleLink;
    }

    public void setScheduleLink(@Nullable String scheduleLink) {
        this.scheduleLink = scheduleLink;
    }

    @Nullable
    public String getScheduleLongLink() {
        return scheduleLongLink;
    }

    public void setScheduleLongLink(@Nullable String scheduleLongLink) {
        this.scheduleLongLink = scheduleLongLink;
    }

    @Nullable
    public String getFirstDeadline() {
        return firstDeadline;
    }

    public void setFirstDeadline(@Nullable String firstDeadline) {
        this.firstDeadline = firstDeadline;
    }

    @Nullable
    public String getLastDeadline() {
        return lastDeadline;
    }

    public void setLastDeadline(@Nullable String lastDeadline) {
        this.lastDeadline = lastDeadline;
    }

    @NotNull
    public List<String> getSubscriptions() {
        if (subscriptions == null) {
            subscriptions = new ArrayList<>();
        }
        return subscriptions;
    }

    public void setSubscriptions(@Nullable List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @NotNull
    public List<Integer> getAnnouncements() {
        if (announcements == null) {
            announcements = new ArrayList<>();
        }
        return announcements;
    }

    public void setAnnouncements(@Nullable List<Integer> announcements) {
        this.announcements = announcements;
    }

    public boolean getIsContest() {
        return isContest;
    }

    public void setIsContest(boolean isContest) {
        this.isContest = isContest;
    }

    public boolean isSelfPaced() {
        return isSelfPaced;
    }

    public void setSelfPaced(boolean selfPaced) {
        isSelfPaced = selfPaced;
    }

    public boolean isIdeaCompatible() {
        return isIdeaCompatible;
    }

    public void setIdeaCompatible(boolean ideaCompatible) {
        isIdeaCompatible = ideaCompatible;
    }

    @Nullable
    public String getLastStep() {
        return lastStep;
    }

    public void setLastStep(@Nullable String lastStep) {
        this.lastStep = lastStep;
    }

    @Nullable
    public Video getIntroVideo() {
        return introVideo;
    }

    public void setIntroVideo(@Nullable Video introVideo) {
        this.introVideo = introVideo;
    }

    @NotNull
    public List<String> getSocialProviders() {
        if (socialProviders == null) {
            socialProviders = new ArrayList<>();
        }
        return socialProviders;
    }

    public void setSocialProviders(@Nullable List<String> socialProviders) {
        this.socialProviders = socialProviders;
    }

    @NotNull
    public List<Integer> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public void setTags(@Nullable List<Integer> tags) {
        this.tags = tags;
    }

    public boolean isHasTutors() {
        return hasTutors;
    }

    public void setHasTutors(boolean hasTutors) {
        this.hasTutors = hasTutors;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isProctored() {
        return isProctored;
    }

    public void setProctored(boolean proctored) {
        isProctored = proctored;
    }

    public int getReviewSummary() {
        return reviewSummary;
    }

    public void setReviewSummary(int reviewSummary) {
        this.reviewSummary = reviewSummary;
    }

    public int getCertificatesCount() {
        return certificatesCount;
    }

    public void setCertificatesCount(int certificatesCount) {
        this.certificatesCount = certificatesCount;
    }

    public int getLearnersCount() {
        return learnersCount;
    }

    public void setLearnersCount(int learnersCount) {
        this.learnersCount = learnersCount;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    public void setLanguage(@Nullable String language) {
        this.language = language;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
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

    @Nullable
    public String getLearnersGroup() {
        return learnersGroup;
    }

    public void setLearnersGroup(@Nullable String learnersGroup) {
        this.learnersGroup = learnersGroup;
    }

    @Nullable
    public String getTestersGroup() {
        return testersGroup;
    }

    public void setTestersGroup(@Nullable String testersGroup) {
        this.testersGroup = testersGroup;
    }

    @Nullable
    public String getModeratorsGroup() {
        return moderatorsGroup;
    }

    public void setModeratorsGroup(@Nullable String moderatorsGroup) {
        this.moderatorsGroup = moderatorsGroup;
    }

    @Nullable
    public String getTeachersGroup() {
        return teachersGroup;
    }

    public void setTeachersGroup(@Nullable String teachersGroup) {
        this.teachersGroup = teachersGroup;
    }

    @Nullable
    public String getAdminsGroup() {
        return adminsGroup;
    }

    public void setAdminsGroup(@Nullable String adminsGroup) {
        this.adminsGroup = adminsGroup;
    }

    public int getDiscussionsCount() {
        return discussionsCount;
    }

    public void setDiscussionsCount(int discussionsCount) {
        this.discussionsCount = discussionsCount;
    }

    @Nullable
    public String getDiscussionProxy() {
        return discussionProxy;
    }

    public void setDiscussionProxy(@Nullable String discussionProxy) {
        this.discussionProxy = discussionProxy;
    }

    @NotNull
    public List<String> getDiscussionThreads() {
        if (discussionThreads == null) {
            discussionThreads = new ArrayList<>();
        }
        return discussionThreads;
    }

    public void setDiscussionThreads(@Nullable List<String> discussionThreads) {
        this.discussionThreads = discussionThreads;
    }

    @NotNull
    public String getLink() {
        return Urls.COURSE + "/" + getId();
    }

    @Contract("null -> false")
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Course course = (Course) o;

        if (isCertificateAutoIssued != course.isCertificateAutoIssued) return false;
        if (certificateRegularThreshold != course.certificateRegularThreshold) return false;
        if (certificateDistinctionThreshold != course.certificateDistinctionThreshold) return false;
        if (totalUnits != course.totalUnits) return false;
        if (enrollment != course.enrollment) return false;
        if (isFavorite != course.isFavorite) return false;
        if (isContest != course.isContest) return false;
        if (isSelfPaced != course.isSelfPaced) return false;
        if (isAdaptive != course.isAdaptive) return false;
        if (isIdeaCompatible != course.isIdeaCompatible) return false;
        if (hasTutors != course.hasTutors) return false;
        if (isEnabled != course.isEnabled) return false;
        if (isProctored != course.isProctored) return false;
        if (reviewSummary != course.reviewSummary) return false;
        if (certificatesCount != course.certificatesCount) return false;
        if (learnersCount != course.learnersCount) return false;
        if (owner != course.owner) return false;
        if (isFeatured != course.isFeatured) return false;
        if (isPublic != course.isPublic) return false;
        if (isActive != course.isActive) return false;
        if (discussionsCount != course.discussionsCount) return false;
        if (summary != null ? !summary.equals(course.summary) : course.summary != null) return false;
        if (workload != null ? !workload.equals(course.workload) : course.workload != null) return false;
        if (cover != null ? !cover.equals(course.cover) : course.cover != null) return false;
        if (intro != null ? !intro.equals(course.intro) : course.intro != null) return false;
        if (courseFormat != null ? !courseFormat.equals(course.courseFormat) : course.courseFormat != null)
            return false;
        if (targetAudience != null ? !targetAudience.equals(course.targetAudience) : course.targetAudience != null)
            return false;
        if (certificateFooter != null ?
                !certificateFooter.equals(course.certificateFooter) :
                course.certificateFooter != null) return false;
        if (certificateCoverOrg != null ?
                !certificateCoverOrg.equals(course.certificateCoverOrg) :
                course.certificateCoverOrg != null) return false;
        if (instructors != null ? !instructors.equals(course.instructors) : course.instructors != null) return false;
        if (certificate != null ? !certificate.equals(course.certificate) : course.certificate != null) return false;
        if (requirements != null ? !requirements.equals(course.requirements) : course.requirements != null)
            return false;
        if (description != null ? !description.equals(course.description) : course.description != null) return false;
        if (sections != null ? !sections.equals(course.sections) : course.sections != null) return false;
        if (actions != null ? !actions.equals(course.actions) : course.actions != null) return false;
        if (progress != null ? !progress.equals(course.progress) : course.progress != null) return false;
        if (certificateLink != null ? !certificateLink.equals(course.certificateLink) : course.certificateLink != null)
            return false;
        if (certificateRegularLink != null ?
                !certificateRegularLink.equals(course.certificateRegularLink) :
                course.certificateRegularLink != null) return false;
        if (certificateDistinctionLink != null ?
                !certificateDistinctionLink.equals(course.certificateDistinctionLink) :
                course.certificateDistinctionLink != null) return false;
        if (scheduleLink != null ? !scheduleLink.equals(course.scheduleLink) : course.scheduleLink != null)
            return false;
        if (scheduleLongLink != null ?
                !scheduleLongLink.equals(course.scheduleLongLink) :
                course.scheduleLongLink != null)
            return false;
        if (firstDeadline != null ? !firstDeadline.equals(course.firstDeadline) : course.firstDeadline != null)
            return false;
        if (lastDeadline != null ? !lastDeadline.equals(course.lastDeadline) : course.lastDeadline != null)
            return false;
        if (subscriptions != null ? !subscriptions.equals(course.subscriptions) : course.subscriptions != null)
            return false;
        if (announcements != null ? !announcements.equals(course.announcements) : course.announcements != null)
            return false;
        if (lastStep != null ? !lastStep.equals(course.lastStep) : course.lastStep != null) return false;
        if (introVideo != null ? !introVideo.equals(course.introVideo) : course.introVideo != null) return false;
        if (socialProviders != null ? !socialProviders.equals(course.socialProviders) : course.socialProviders != null)
            return false;
        if (authors != null ? !authors.equals(course.authors) : course.authors != null) return false;
        if (tags != null ? !tags.equals(course.tags) : course.tags != null) return false;
        if (language != null ? !language.equals(course.language) : course.language != null) return false;
        if (title != null ? !title.equals(course.title) : course.title != null) return false;
        if (slug != null ? !slug.equals(course.slug) : course.slug != null) return false;
        if (beginDate != null ? !beginDate.equals(course.beginDate) : course.beginDate != null) return false;
        if (endDate != null ? !endDate.equals(course.endDate) : course.endDate != null) return false;
        if (softDeadline != null ? !softDeadline.equals(course.softDeadline) : course.softDeadline != null)
            return false;
        if (hardDeadline != null ? !hardDeadline.equals(course.hardDeadline) : course.hardDeadline != null)
            return false;
        if (gradingPolicy != null ? !gradingPolicy.equals(course.gradingPolicy) : course.gradingPolicy != null)
            return false;
        if (beginDateSource != null ? !beginDateSource.equals(course.beginDateSource) : course.beginDateSource != null)
            return false;
        if (endDateSource != null ? !endDateSource.equals(course.endDateSource) : course.endDateSource != null)
            return false;
        if (softDeadlineSource != null ?
                !softDeadlineSource.equals(course.softDeadlineSource) :
                course.softDeadlineSource != null) return false;
        if (hardDeadlineSource != null ?
                !hardDeadlineSource.equals(course.hardDeadlineSource) :
                course.hardDeadlineSource != null) return false;
        if (gradingPolicySource != null ?
                !gradingPolicySource.equals(course.gradingPolicySource) :
                course.gradingPolicySource != null) return false;
        if (createDate != null ? !createDate.equals(course.createDate) : course.createDate != null) return false;
        if (updateDate != null ? !updateDate.equals(course.updateDate) : course.updateDate != null) return false;
        if (learnersGroup != null ? !learnersGroup.equals(course.learnersGroup) : course.learnersGroup != null)
            return false;
        if (testersGroup != null ? !testersGroup.equals(course.testersGroup) : course.testersGroup != null)
            return false;
        if (moderatorsGroup != null ? !moderatorsGroup.equals(course.moderatorsGroup) : course.moderatorsGroup != null)
            return false;
        if (teachersGroup != null ? !teachersGroup.equals(course.teachersGroup) : course.teachersGroup != null)
            return false;
        if (adminsGroup != null ? !adminsGroup.equals(course.adminsGroup) : course.adminsGroup != null) return false;
        //noinspection SimplifiableIfStatement
        if (discussionProxy != null ? !discussionProxy.equals(course.discussionProxy) : course.discussionProxy != null)
            return false;
        return discussionThreads != null ?
                discussionThreads.equals(course.discussionThreads) :
                course.discussionThreads == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (workload != null ? workload.hashCode() : 0);
        result = 31 * result + (cover != null ? cover.hashCode() : 0);
        result = 31 * result + (intro != null ? intro.hashCode() : 0);
        result = 31 * result + (courseFormat != null ? courseFormat.hashCode() : 0);
        result = 31 * result + (targetAudience != null ? targetAudience.hashCode() : 0);
        result = 31 * result + (certificateFooter != null ? certificateFooter.hashCode() : 0);
        result = 31 * result + (certificateCoverOrg != null ? certificateCoverOrg.hashCode() : 0);
        result = 31 * result + (isCertificateAutoIssued ? 1 : 0);
        result = 31 * result + certificateRegularThreshold;
        result = 31 * result + certificateDistinctionThreshold;
        result = 31 * result + (instructors != null ? instructors.hashCode() : 0);
        result = 31 * result + (certificate != null ? certificate.hashCode() : 0);
        result = 31 * result + (requirements != null ? requirements.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (sections != null ? sections.hashCode() : 0);
        result = 31 * result + totalUnits;
        result = 31 * result + enrollment;
        result = 31 * result + (isFavorite ? 1 : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (progress != null ? progress.hashCode() : 0);
        result = 31 * result + (certificateLink != null ? certificateLink.hashCode() : 0);
        result = 31 * result + (certificateRegularLink != null ? certificateRegularLink.hashCode() : 0);
        result = 31 * result + (certificateDistinctionLink != null ? certificateDistinctionLink.hashCode() : 0);
        result = 31 * result + (scheduleLink != null ? scheduleLink.hashCode() : 0);
        result = 31 * result + (scheduleLongLink != null ? scheduleLongLink.hashCode() : 0);
        result = 31 * result + (firstDeadline != null ? firstDeadline.hashCode() : 0);
        result = 31 * result + (lastDeadline != null ? lastDeadline.hashCode() : 0);
        result = 31 * result + (subscriptions != null ? subscriptions.hashCode() : 0);
        result = 31 * result + (announcements != null ? announcements.hashCode() : 0);
        result = 31 * result + (isContest ? 1 : 0);
        result = 31 * result + (isSelfPaced ? 1 : 0);
        result = 31 * result + (isAdaptive ? 1 : 0);
        result = 31 * result + (isIdeaCompatible ? 1 : 0);
        result = 31 * result + (lastStep != null ? lastStep.hashCode() : 0);
        result = 31 * result + (introVideo != null ? introVideo.hashCode() : 0);
        result = 31 * result + (socialProviders != null ? socialProviders.hashCode() : 0);
        result = 31 * result + (authors != null ? authors.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (hasTutors ? 1 : 0);
        result = 31 * result + (isEnabled ? 1 : 0);
        result = 31 * result + (isProctored ? 1 : 0);
        result = 31 * result + reviewSummary;
        result = 31 * result + certificatesCount;
        result = 31 * result + learnersCount;
        result = 31 * result + owner;
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (isFeatured ? 1 : 0);
        result = 31 * result + (isPublic ? 1 : 0);
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
        result = 31 * result + (learnersGroup != null ? learnersGroup.hashCode() : 0);
        result = 31 * result + (testersGroup != null ? testersGroup.hashCode() : 0);
        result = 31 * result + (moderatorsGroup != null ? moderatorsGroup.hashCode() : 0);
        result = 31 * result + (teachersGroup != null ? teachersGroup.hashCode() : 0);
        result = 31 * result + (adminsGroup != null ? adminsGroup.hashCode() : 0);
        result = 31 * result + discussionsCount;
        result = 31 * result + (discussionProxy != null ? discussionProxy.hashCode() : 0);
        result = 31 * result + (discussionThreads != null ? discussionThreads.hashCode() : 0);
        return result;
    }
}
