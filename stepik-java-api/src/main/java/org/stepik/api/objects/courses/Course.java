package org.stepik.api.objects.courses;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.StudyObject;
import org.stepik.api.objects.steps.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author meanmail
 */
public class Course extends StudyObject {
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
    private List<Long> sections;
    @SerializedName("total_units")
    private int totalUnits;
    private int enrollment;
    @SerializedName("is_favorite")
    private boolean isFavorite;
    private Map<String, String> actions;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Course course = (Course) o;
        return isCertificateAutoIssued == course.isCertificateAutoIssued &&
                certificateRegularThreshold == course.certificateRegularThreshold &&
                certificateDistinctionThreshold == course.certificateDistinctionThreshold &&
                totalUnits == course.totalUnits &&
                enrollment == course.enrollment &&
                isFavorite == course.isFavorite &&
                isContest == course.isContest &&
                isSelfPaced == course.isSelfPaced &&
                isIdeaCompatible == course.isIdeaCompatible &&
                hasTutors == course.hasTutors &&
                isEnabled == course.isEnabled &&
                isProctored == course.isProctored &&
                reviewSummary == course.reviewSummary &&
                certificatesCount == course.certificatesCount &&
                learnersCount == course.learnersCount &&
                owner == course.owner &&
                isFeatured == course.isFeatured &&
                isPublic == course.isPublic &&
                isActive == course.isActive &&
                discussionsCount == course.discussionsCount &&
                Objects.equals(summary, course.summary) &&
                Objects.equals(workload, course.workload) &&
                Objects.equals(cover, course.cover) &&
                Objects.equals(intro, course.intro) &&
                Objects.equals(courseFormat, course.courseFormat) &&
                Objects.equals(targetAudience, course.targetAudience) &&
                Objects.equals(certificateFooter, course.certificateFooter) &&
                Objects.equals(certificateCoverOrg, course.certificateCoverOrg) &&
                Objects.equals(instructors, course.instructors) &&
                Objects.equals(certificate, course.certificate) &&
                Objects.equals(requirements, course.requirements) &&
                Objects.equals(sections, course.sections) &&
                Objects.equals(actions, course.actions) &&
                Objects.equals(certificateLink, course.certificateLink) &&
                Objects.equals(certificateRegularLink, course.certificateRegularLink) &&
                Objects.equals(certificateDistinctionLink, course.certificateDistinctionLink) &&
                Objects.equals(scheduleLink, course.scheduleLink) &&
                Objects.equals(scheduleLongLink, course.scheduleLongLink) &&
                Objects.equals(firstDeadline, course.firstDeadline) &&
                Objects.equals(lastDeadline, course.lastDeadline) &&
                Objects.equals(subscriptions, course.subscriptions) &&
                Objects.equals(announcements, course.announcements) &&
                Objects.equals(lastStep, course.lastStep) &&
                Objects.equals(introVideo, course.introVideo) &&
                Objects.equals(socialProviders, course.socialProviders) &&
                Objects.equals(authors, course.authors) &&
                Objects.equals(tags, course.tags) &&
                Objects.equals(language, course.language) &&
                Objects.equals(slug, course.slug) &&
                Objects.equals(beginDate, course.beginDate) &&
                Objects.equals(endDate, course.endDate) &&
                Objects.equals(softDeadline, course.softDeadline) &&
                Objects.equals(hardDeadline, course.hardDeadline) &&
                Objects.equals(gradingPolicy, course.gradingPolicy) &&
                Objects.equals(beginDateSource, course.beginDateSource) &&
                Objects.equals(endDateSource, course.endDateSource) &&
                Objects.equals(softDeadlineSource, course.softDeadlineSource) &&
                Objects.equals(hardDeadlineSource, course.hardDeadlineSource) &&
                Objects.equals(gradingPolicySource, course.gradingPolicySource) &&
                Objects.equals(learnersGroup, course.learnersGroup) &&
                Objects.equals(testersGroup, course.testersGroup) &&
                Objects.equals(moderatorsGroup, course.moderatorsGroup) &&
                Objects.equals(teachersGroup, course.teachersGroup) &&
                Objects.equals(adminsGroup, course.adminsGroup) &&
                Objects.equals(discussionProxy, course.discussionProxy) &&
                Objects.equals(discussionThreads, course.discussionThreads);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(),
                summary,
                workload,
                cover,
                intro,
                courseFormat,
                targetAudience,
                certificateFooter,
                certificateCoverOrg,
                isCertificateAutoIssued,
                certificateRegularThreshold,
                certificateDistinctionThreshold,
                instructors,
                certificate,
                requirements,
                sections,
                totalUnits,
                enrollment,
                isFavorite,
                actions,
                certificateLink,
                certificateRegularLink,
                certificateDistinctionLink,
                scheduleLink,
                scheduleLongLink,
                firstDeadline,
                lastDeadline,
                subscriptions,
                announcements,
                isContest,
                isSelfPaced,
                isIdeaCompatible,
                lastStep,
                introVideo,
                socialProviders,
                authors,
                tags,
                hasTutors,
                isEnabled,
                isProctored,
                reviewSummary,
                certificatesCount,
                learnersCount,
                owner,
                language,
                isFeatured,
                isPublic,
                slug,
                beginDate,
                endDate,
                softDeadline,
                hardDeadline,
                gradingPolicy,
                beginDateSource,
                endDateSource,
                softDeadlineSource,
                hardDeadlineSource,
                gradingPolicySource,
                isActive,
                learnersGroup,
                testersGroup,
                moderatorsGroup,
                teachersGroup,
                adminsGroup,
                discussionsCount,
                discussionProxy,
                discussionThreads);
    }
}
