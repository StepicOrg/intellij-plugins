package org.stepik.api.objects.lessons;

import com.google.gson.annotations.SerializedName;

/**
 * @author meanmail
 */
public class Lesson {
    private int id;
    private int[] steps;
    private Object actions;
    private int[] tags;
    @SerializedName("required_tags")
    private int[] requiredTags;
    private Object[] playlists;
    @SerializedName("is_prime")
    private boolean isPrime;
    private String progress;
    private String[] subscriptions;
    @SerializedName("viewed_by")
    private int viewedBy;
    @SerializedName("passed_by")
    private int passedBy;
    private String[] dependencies;
    private String[] followers;
    @SerializedName("time_to_complete")
    private int timeToComplete;
    @SerializedName("cover_url")
    private String coverUrl;
    @SerializedName("is_comments_enabled")
    private boolean isCommentsEnabled;
    private int owner;
    private String language;
    @SerializedName("is_featured")
    private boolean isFeatured;
    @SerializedName("is_public")
    private boolean isPublic;
    private String title;
    private String slug;
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
    @SerializedName("epic_count")
    private int epicCount;
    @SerializedName("abuse_count")
    private int abuseCount;
    private String vote;
}
