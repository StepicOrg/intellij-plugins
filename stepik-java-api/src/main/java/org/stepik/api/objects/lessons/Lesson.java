package org.stepik.api.objects.lessons;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.StudyObject;
import org.stepik.api.urls.Urls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public class Lesson extends StudyObject {
    private List<Long> steps;
    private Map<String, String> actions;
    private List<Integer> tags;
    @SerializedName("required_tags")
    private List<Integer> requiredTags;
    private List<Object> playlists;
    @SerializedName("is_prime")
    private boolean isPrime;
    private String progress;
    private List<String> subscriptions;
    @SerializedName("viewed_by")
    private int viewedBy;
    @SerializedName("passed_by")
    private int passedBy;
    private List<String> dependencies;
    private List<String> followers;
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
    private List<String> discussionThreads;
    @SerializedName("epic_count")
    private int epicCount;
    @SerializedName("abuse_count")
    private int abuseCount;
    private String vote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Lesson lesson = (Lesson) o;

        if (isPrime != lesson.isPrime) return false;
        if (viewedBy != lesson.viewedBy) return false;
        if (passedBy != lesson.passedBy) return false;
        if (timeToComplete != lesson.timeToComplete) return false;
        if (isCommentsEnabled != lesson.isCommentsEnabled) return false;
        if (owner != lesson.owner) return false;
        if (isFeatured != lesson.isFeatured) return false;
        if (isPublic != lesson.isPublic) return false;
        if (discussionsCount != lesson.discussionsCount) return false;
        if (epicCount != lesson.epicCount) return false;
        if (abuseCount != lesson.abuseCount) return false;
        if (steps != null ? !steps.equals(lesson.steps) : lesson.steps != null) return false;
        if (actions != null ? !actions.equals(lesson.actions) : lesson.actions != null) return false;
        if (tags != null ? !tags.equals(lesson.tags) : lesson.tags != null) return false;
        if (requiredTags != null ? !requiredTags.equals(lesson.requiredTags) : lesson.requiredTags != null)
            return false;
        if (playlists != null ? !playlists.equals(lesson.playlists) : lesson.playlists != null) return false;
        if (progress != null ? !progress.equals(lesson.progress) : lesson.progress != null) return false;
        if (subscriptions != null ? !subscriptions.equals(lesson.subscriptions) : lesson.subscriptions != null)
            return false;
        if (dependencies != null ? !dependencies.equals(lesson.dependencies) : lesson.dependencies != null)
            return false;
        if (followers != null ? !followers.equals(lesson.followers) : lesson.followers != null) return false;
        if (coverUrl != null ? !coverUrl.equals(lesson.coverUrl) : lesson.coverUrl != null) return false;
        if (language != null ? !language.equals(lesson.language) : lesson.language != null) return false;
        if (title != null ? !title.equals(lesson.title) : lesson.title != null) return false;
        if (slug != null ? !slug.equals(lesson.slug) : lesson.slug != null) return false;
        if (createDate != null ? !createDate.equals(lesson.createDate) : lesson.createDate != null) return false;
        if (updateDate != null ? !updateDate.equals(lesson.updateDate) : lesson.updateDate != null) return false;
        if (learnersGroup != null ? !learnersGroup.equals(lesson.learnersGroup) : lesson.learnersGroup != null)
            return false;
        if (testersGroup != null ? !testersGroup.equals(lesson.testersGroup) : lesson.testersGroup != null)
            return false;
        if (moderatorsGroup != null ? !moderatorsGroup.equals(lesson.moderatorsGroup) : lesson.moderatorsGroup != null)
            return false;
        if (teachersGroup != null ? !teachersGroup.equals(lesson.teachersGroup) : lesson.teachersGroup != null)
            return false;
        if (adminsGroup != null ? !adminsGroup.equals(lesson.adminsGroup) : lesson.adminsGroup != null) return false;
        if (discussionProxy != null ? !discussionProxy.equals(lesson.discussionProxy) : lesson.discussionProxy != null)
            return false;
        //noinspection SimplifiableIfStatement
        if (discussionThreads != null ?
                !discussionThreads.equals(lesson.discussionThreads) :
                lesson.discussionThreads != null) return false;
        return vote != null ? vote.equals(lesson.vote) : lesson.vote == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (steps != null ? steps.hashCode() : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (requiredTags != null ? requiredTags.hashCode() : 0);
        result = 31 * result + (playlists != null ? playlists.hashCode() : 0);
        result = 31 * result + (isPrime ? 1 : 0);
        result = 31 * result + (progress != null ? progress.hashCode() : 0);
        result = 31 * result + (subscriptions != null ? subscriptions.hashCode() : 0);
        result = 31 * result + viewedBy;
        result = 31 * result + passedBy;
        result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
        result = 31 * result + (followers != null ? followers.hashCode() : 0);
        result = 31 * result + timeToComplete;
        result = 31 * result + (coverUrl != null ? coverUrl.hashCode() : 0);
        result = 31 * result + (isCommentsEnabled ? 1 : 0);
        result = 31 * result + owner;
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (isFeatured ? 1 : 0);
        result = 31 * result + (isPublic ? 1 : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (slug != null ? slug.hashCode() : 0);
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
        result = 31 * result + epicCount;
        result = 31 * result + abuseCount;
        result = 31 * result + (vote != null ? vote.hashCode() : 0);
        return result;
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
    @Override
    public String getDescription() {
        return String.format("Lesson %s/lesson/%d", Urls.STEPIK_URL, getId());
    }

    @NotNull
    public List<Long> getSteps() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        return steps;
    }

    public void setSteps(@Nullable List<Long> steps) {
        this.steps = steps;
    }

    @Nullable
    public Map<String, String> getActions() {
        return actions;
    }

    public void setActions(@Nullable Map<String, String> actions) {
        this.actions = actions;
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

    @NotNull
    public List<Integer> getRequiredTags() {
        if (requiredTags == null) {
            requiredTags = new ArrayList<>();
        }
        return requiredTags;
    }

    public void setRequiredTags(@Nullable List<Integer> requiredTags) {
        this.requiredTags = requiredTags;
    }

    @NotNull
    public List<Object> getPlaylists() {
        if (playlists == null) {
            playlists = new ArrayList<>();
        }
        return playlists;
    }

    public void setPlaylists(@Nullable List<Object> playlists) {
        this.playlists = playlists;
    }

    public boolean isPrime() {
        return isPrime;
    }

    public void setPrime(boolean prime) {
        isPrime = prime;
    }

    @Nullable
    public String getProgress() {
        return progress;
    }

    public void setProgress(@Nullable String progress) {
        this.progress = progress;
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

    public int getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(int viewedBy) {
        this.viewedBy = viewedBy;
    }

    public int getPassedBy() {
        return passedBy;
    }

    public void setPassedBy(int passedBy) {
        this.passedBy = passedBy;
    }

    @NotNull
    public List<String> getDependencies() {
        if (dependencies == null) {
            dependencies = new ArrayList<>();
        }
        return dependencies;
    }

    public void setDependencies(@Nullable List<String> dependencies) {
        this.dependencies = dependencies;
    }

    @NotNull
    public List<String> getFollowers() {
        if (followers == null) {
            followers = new ArrayList<>();
        }
        return followers;
    }

    public void setFollowers(@Nullable List<String> followers) {
        this.followers = followers;
    }

    public int getTimeToComplete() {
        return timeToComplete;
    }

    public void setTimeToComplete(int timeToComplete) {
        this.timeToComplete = timeToComplete;
    }

    @Nullable
    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(@Nullable String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public boolean isCommentsEnabled() {
        return isCommentsEnabled;
    }

    public void setCommentsEnabled(boolean commentsEnabled) {
        isCommentsEnabled = commentsEnabled;
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
    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(@Nullable String createDate) {
        this.createDate = createDate;
    }

    @NotNull
    public String getUpdateDate() {
        if (updateDate == null) {
            updateDate = "";
        }
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

    public int getEpicCount() {
        return epicCount;
    }

    public void setEpicCount(int epicCount) {
        this.epicCount = epicCount;
    }

    public int getAbuseCount() {
        return abuseCount;
    }

    public void setAbuseCount(int abuseCount) {
        this.abuseCount = abuseCount;
    }

    @Nullable
    public String getVote() {
        return vote;
    }

    public void setVote(@Nullable String vote) {
        this.vote = vote;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
