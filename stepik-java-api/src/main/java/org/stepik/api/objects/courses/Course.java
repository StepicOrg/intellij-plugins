package org.stepik.api.objects.courses;

import com.google.gson.annotations.SerializedName;
import org.stepik.api.objects.steps.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class Course {
    private int id;
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
    private List<Integer> sections;
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
    private List<Integer> authors;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAdaptive() {
        return isAdaptive;
    }

    public void setAdaptive(boolean adaptive) {
        isAdaptive = adaptive;
    }

    public List<Integer> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public void setAuthors(List<Integer> authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    public void setSections(List<Integer> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCourseFormat() {
        return courseFormat;
    }

    public void setCourseFormat(String courseFormat) {
        this.courseFormat = courseFormat;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getCertificateFooter() {
        return certificateFooter;
    }

    public void setCertificateFooter(String certificateFooter) {
        this.certificateFooter = certificateFooter;
    }

    public String getCertificateCoverOrg() {
        return certificateCoverOrg;
    }

    public void setCertificateCoverOrg(String certificateCoverOrg) {
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

    public List<Integer> getInstructors() {
        if (instructors == null) {
            instructors = new ArrayList<>();
        }
        return instructors;
    }

    public void setInstructors(List<Integer> instructors) {
        this.instructors = instructors;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
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

    public Map<String, String> getActions() {
        return actions;
    }

    public void setActions(Map<String, String> actions) {
        this.actions = actions;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getCertificateLink() {
        return certificateLink;
    }

    public void setCertificateLink(String certificateLink) {
        this.certificateLink = certificateLink;
    }

    public String getCertificateRegularLink() {
        return certificateRegularLink;
    }

    public void setCertificateRegularLink(String certificateRegularLink) {
        this.certificateRegularLink = certificateRegularLink;
    }

    public String getCertificateDistinctionLink() {
        return certificateDistinctionLink;
    }

    public void setCertificateDistinctionLink(String certificateDistinctionLink) {
        this.certificateDistinctionLink = certificateDistinctionLink;
    }

    public String getScheduleLink() {
        return scheduleLink;
    }

    public void setScheduleLink(String scheduleLink) {
        this.scheduleLink = scheduleLink;
    }

    public String getScheduleLongLink() {
        return scheduleLongLink;
    }

    public void setScheduleLongLink(String scheduleLongLink) {
        this.scheduleLongLink = scheduleLongLink;
    }

    public String getFirstDeadline() {
        return firstDeadline;
    }

    public void setFirstDeadline(String firstDeadline) {
        this.firstDeadline = firstDeadline;
    }

    public String getLastDeadline() {
        return lastDeadline;
    }

    public void setLastDeadline(String lastDeadline) {
        this.lastDeadline = lastDeadline;
    }

    public List<String> getSubscriptions() {
        if (subscriptions == null) {
            subscriptions = new ArrayList<>();
        }
        return subscriptions;
    }

    public void setSubscriptions(List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Integer> getAnnouncements() {
        if (announcements == null) {
            announcements = new ArrayList<>();
        }
        return announcements;
    }

    public void setAnnouncements(List<Integer> announcements) {
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

    public String getLastStep() {
        return lastStep;
    }

    public void setLastStep(String lastStep) {
        this.lastStep = lastStep;
    }

    public Video getIntroVideo() {
        return introVideo;
    }

    public void setIntroVideo(Video introVideo) {
        this.introVideo = introVideo;
    }

    public List<String> getSocialProviders() {
        if (socialProviders == null) {
            socialProviders = new ArrayList<>();
        }
        return socialProviders;
    }

    public void setSocialProviders(List<String> socialProviders) {
        this.socialProviders = socialProviders;
    }

    public List<Integer> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public void setTags(List<Integer> tags) {
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
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

    public String getLearnersGroup() {
        return learnersGroup;
    }

    public void setLearnersGroup(String learnersGroup) {
        this.learnersGroup = learnersGroup;
    }

    public String getTestersGroup() {
        return testersGroup;
    }

    public void setTestersGroup(String testersGroup) {
        this.testersGroup = testersGroup;
    }

    public String getModeratorsGroup() {
        return moderatorsGroup;
    }

    public void setModeratorsGroup(String moderatorsGroup) {
        this.moderatorsGroup = moderatorsGroup;
    }

    public String getTeachersGroup() {
        return teachersGroup;
    }

    public void setTeachersGroup(String teachersGroup) {
        this.teachersGroup = teachersGroup;
    }

    public String getAdminsGroup() {
        return adminsGroup;
    }

    public void setAdminsGroup(String adminsGroup) {
        this.adminsGroup = adminsGroup;
    }

    public int getDiscussionsCount() {
        return discussionsCount;
    }

    public void setDiscussionsCount(int discussionsCount) {
        this.discussionsCount = discussionsCount;
    }

    public String getDiscussionProxy() {
        return discussionProxy;
    }

    public void setDiscussionProxy(String discussionProxy) {
        this.discussionProxy = discussionProxy;
    }

    public List<String> getDiscussionThreads() {
        if (discussionThreads == null) {
            discussionThreads = new ArrayList<>();
        }
        return discussionThreads;
    }

    public void setDiscussionThreads(List<String> discussionThreads) {
        this.discussionThreads = discussionThreads;
    }
}
