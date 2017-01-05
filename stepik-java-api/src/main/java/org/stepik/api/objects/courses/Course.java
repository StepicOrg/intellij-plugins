package org.stepik.api.objects.courses;

import com.google.gson.annotations.SerializedName;

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
    private String[] instructors;
    private String certificate;
    private String requirements;
    private String description;
    private int[] sections;
    @SerializedName("total_units")
    private String totalUnits;
    private String enrollment;
    @SerializedName("is_favorite")
    private String isFavorite;
    private Object actions;
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
    private String[] subscriptions;
    private String[] announcements;
    @SerializedName("is_contest")
    private String isContest;
    @SerializedName("is_self_paced")
    private boolean isSelfPaced;
    @SerializedName("is_adaptive")
    private boolean isAdaptive;
    @SerializedName("is_idea_compatible")
    private boolean isIdeaCompatible;
    @SerializedName("last_step")
    private String lastStep;
    @SerializedName("intro_video")
    private Object introVideo;
    @SerializedName("social_providers")
    private String[] socialProviders;
    private String[] authors;
    private String[] tags;
    @SerializedName("has_tutors")
    private boolean hasTutors;
    @SerializedName("is_enabled")
    private boolean isEnabled;
    @SerializedName("is_proctored")
    private boolean isProctored;
    @SerializedName("review_summary")
    private String reviewSummary;
    @SerializedName("certificates_count")
    private int certificatesCount;
    @SerializedName("learners_count")
    private int learnersCount;
    private String owner;
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
    private String[] discussionThreads;
}
