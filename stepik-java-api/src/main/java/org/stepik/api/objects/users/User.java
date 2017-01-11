package org.stepik.api.objects.users;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class User {
    private int id;
    private int profile;
    @SerializedName("is_private")
    private boolean isPrivate;
    @SerializedName("is_active")
    private boolean isActive;
    @SerializedName("is_guest")
    private boolean isGuest = true;
    @SerializedName("is_organization")
    private boolean isOrganization;
    @SerializedName("short_bio")
    private String shortBio;
    private String details;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private String alias;
    private String avatar;
    private String cover;
    private int level;
    @SerializedName("level_title")
    private String levelTitle;
    @SerializedName("tag_progresses")
    private List<Integer> tagProgresses;
    private int knowledge;
    private int knowledgeRank;
    private int reputation;
    @SerializedName("reputation_rank")
    private int reputationRank;
    @SerializedName("join_date")
    private String joinDate;
    @SerializedName("social_profiles")
    private List<Integer> socialProfiles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public String getFirstName() {
        if (firstName == null) {
            firstName = "";
        }
        return firstName;
    }

    public void setFirstName(@Nullable String firstName) {
        this.firstName = firstName;
    }

    @NotNull
    public String getLastName() {
        if (lastName == null) {
            lastName = "";
        }
        return lastName;
    }

    public void setLastName(@Nullable String lastName) {
        this.lastName = lastName;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public boolean isOrganization() {
        return isOrganization;
    }

    public void setOrganization(boolean organization) {
        isOrganization = organization;
    }

    @Nullable
    public String getShortBio() {
        return shortBio;
    }

    public void setShortBio(@Nullable String shortBio) {
        this.shortBio = shortBio;
    }

    @Nullable
    public String getDetails() {
        return details;
    }

    public void setDetails(@Nullable String details) {
        this.details = details;
    }

    @Nullable
    public String getAlias() {
        return alias;
    }

    public void setAlias(@Nullable String alias) {
        this.alias = alias;
    }

    @Nullable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(@Nullable String avatar) {
        this.avatar = avatar;
    }

    @Nullable
    public String getCover() {
        return cover;
    }

    public void setCover(@Nullable String cover) {
        this.cover = cover;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Nullable
    public String getLevelTitle() {
        return levelTitle;
    }

    public void setLevelTitle(@Nullable String levelTitle) {
        this.levelTitle = levelTitle;
    }

    @Nullable
    public List<Integer> getTagProgresses() {
        return tagProgresses;
    }

    public void setTagProgresses(@Nullable List<Integer> tagProgresses) {
        this.tagProgresses = tagProgresses;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(int knowledge) {
        this.knowledge = knowledge;
    }

    public int getKnowledgeRank() {
        return knowledgeRank;
    }

    public void setKnowledgeRank(int knowledgeRank) {
        this.knowledgeRank = knowledgeRank;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public int getReputationRank() {
        return reputationRank;
    }

    public void setReputationRank(int reputationRank) {
        this.reputationRank = reputationRank;
    }

    @Nullable
    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(@Nullable String joinDate) {
        this.joinDate = joinDate;
    }

    @NotNull
    public List<Integer> getSocialProfiles() {
        if (socialProfiles == null) {
            socialProfiles = new ArrayList<>();
        }
        return socialProfiles;
    }

    public void setSocialProfiles(@Nullable List<Integer> socialProfiles) {
        this.socialProfiles = socialProfiles;
    }
}
