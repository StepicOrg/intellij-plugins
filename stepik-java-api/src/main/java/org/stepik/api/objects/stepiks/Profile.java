package org.stepik.api.objects.stepiks;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author meanmail
 */
public class Profile {
    private int id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("is_private")
    private boolean isPrivate;
    private String avatar;
    private String language;
    private int city;
    @SerializedName("short_bio")
    private String shortBio;
    private String details;
    @SerializedName("subscribed_for_mail")
    private boolean subscribedForMail;
    @SerializedName("notification_email_delay")
    private String notificationEmailDelay;
    @SerializedName("notification_status")
    private int notificationStatus;
    @SerializedName("is_staff")
    private boolean isStaff;
    @SerializedName("is_guest")
    private boolean isGuest;
    @SerializedName("can_add_lesson")
    private boolean canAddLesson;
    @SerializedName("can_add_course")
    private boolean canAddCourse;
    @SerializedName("can_add_group")
    private boolean canAddGroup;
    @SerializedName("subscribed_for_news_en")
    private boolean subscribedForNewsEn;
    @SerializedName("subscribed_for_news_ru")
    private boolean subscribedForNewsRu;
    @SerializedName("bit_field")
    private int bitField;
    private int level;
    @SerializedName("level_title")
    private String levelTitle;
    @SerializedName("level_abilities")
    private List<String> levelAbilities;
    @SerializedName("has_password")
    private boolean hasPassword;
    @SerializedName("social_accounts")
    private List<Integer> socialAccounts;
    @SerializedName("email_addresses")
    private List<Integer> emailAddresses;
    @SerializedName("is_email_verified")
    private boolean isEmailVerified;
    @SerializedName("invite_url")
    private String inviteUrl;
    @SerializedName("telegram_bot_url")
    private String telegramBotUrl;
    private String balance;
    @SerializedName("subscription_plan")
    private String subscriptionPlan;
}
