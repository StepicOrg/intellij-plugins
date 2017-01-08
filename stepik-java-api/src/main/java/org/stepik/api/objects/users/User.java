package org.stepik.api.objects.users;

import com.google.gson.annotations.SerializedName;

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
    private boolean isGuest;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
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

    public String getShortBio() {
        return shortBio;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelTitle() {
        return levelTitle;
    }

    public void setLevelTitle(String levelTitle) {
        this.levelTitle = levelTitle;
    }

    public List<Integer> getTagProgresses() {
        return tagProgresses;
    }

    public void setTagProgresses(List<Integer> tagProgresses) {
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

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public List<Integer> getSocialProfiles() {
        if (socialProfiles == null) {
            socialProfiles = new ArrayList<>();
        }
        return socialProfiles;
    }

    public void setSocialProfiles(List<Integer> socialProfiles) {
        this.socialProfiles = socialProfiles;
    }
}
