package org.stepik.api.objects.users;

import com.google.gson.annotations.SerializedName;

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
    private int[] tagProgresses;
    private int knowledge;
    private int knowledgeRank;
    private int reputation;
    @SerializedName("reputation_rank")
    private int reputationRank;
    @SerializedName("join_date")
    private String joinDate;
    @SerializedName("social_profiles")
    private int[] socialProfiles;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
