package org.stepik.api.objects.users;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class User extends AbstractObject {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (profile != user.profile) return false;
        if (isPrivate != user.isPrivate) return false;
        if (isActive != user.isActive) return false;
        if (isGuest != user.isGuest) return false;
        if (isOrganization != user.isOrganization) return false;
        if (level != user.level) return false;
        if (knowledge != user.knowledge) return false;
        if (knowledgeRank != user.knowledgeRank) return false;
        if (reputation != user.reputation) return false;
        if (reputationRank != user.reputationRank) return false;
        if (shortBio != null ? !shortBio.equals(user.shortBio) : user.shortBio != null) return false;
        if (details != null ? !details.equals(user.details) : user.details != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (alias != null ? !alias.equals(user.alias) : user.alias != null) return false;
        if (avatar != null ? !avatar.equals(user.avatar) : user.avatar != null) return false;
        if (cover != null ? !cover.equals(user.cover) : user.cover != null) return false;
        if (levelTitle != null ? !levelTitle.equals(user.levelTitle) : user.levelTitle != null) return false;
        if (tagProgresses != null ? !tagProgresses.equals(user.tagProgresses) : user.tagProgresses != null)
            return false;
        //noinspection SimplifiableIfStatement
        if (joinDate != null ? !joinDate.equals(user.joinDate) : user.joinDate != null) return false;
        return socialProfiles != null ? socialProfiles.equals(user.socialProfiles) : user.socialProfiles == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + profile;
        result = 31 * result + (isPrivate ? 1 : 0);
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + (isGuest ? 1 : 0);
        result = 31 * result + (isOrganization ? 1 : 0);
        result = 31 * result + (shortBio != null ? shortBio.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + (cover != null ? cover.hashCode() : 0);
        result = 31 * result + level;
        result = 31 * result + (levelTitle != null ? levelTitle.hashCode() : 0);
        result = 31 * result + (tagProgresses != null ? tagProgresses.hashCode() : 0);
        result = 31 * result + knowledge;
        result = 31 * result + knowledgeRank;
        result = 31 * result + reputation;
        result = 31 * result + reputationRank;
        result = 31 * result + (joinDate != null ? joinDate.hashCode() : 0);
        result = 31 * result + (socialProfiles != null ? socialProfiles.hashCode() : 0);
        return result;
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

    @NotNull
    public List<Integer> getTagProgresses() {
        if (tagProgresses == null) {
            tagProgresses = new ArrayList<>();
        }
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
