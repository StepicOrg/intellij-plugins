package org.stepik.api.objects.stepiks;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

import java.util.List;

/**
 * @author meanmail
 */
public class Profile extends AbstractObject {
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

    @Nullable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Nullable String firstName) {
        this.firstName = firstName;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@Nullable String lastName) {
        this.lastName = lastName;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    @Nullable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(@Nullable String avatar) {
        this.avatar = avatar;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    public void setLanguage(@Nullable String language) {
        this.language = language;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
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

    public boolean isSubscribedForMail() {
        return subscribedForMail;
    }

    public void setSubscribedForMail(boolean subscribedForMail) {
        this.subscribedForMail = subscribedForMail;
    }

    @Nullable
    public String getNotificationEmailDelay() {
        return notificationEmailDelay;
    }

    public void setNotificationEmailDelay(@Nullable String notificationEmailDelay) {
        this.notificationEmailDelay = notificationEmailDelay;
    }

    public int getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(int notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public boolean isCanAddLesson() {
        return canAddLesson;
    }

    public void setCanAddLesson(boolean canAddLesson) {
        this.canAddLesson = canAddLesson;
    }

    public boolean isCanAddCourse() {
        return canAddCourse;
    }

    public void setCanAddCourse(boolean canAddCourse) {
        this.canAddCourse = canAddCourse;
    }

    public boolean isCanAddGroup() {
        return canAddGroup;
    }

    public void setCanAddGroup(boolean canAddGroup) {
        this.canAddGroup = canAddGroup;
    }

    public boolean isSubscribedForNewsEn() {
        return subscribedForNewsEn;
    }

    public void setSubscribedForNewsEn(boolean subscribedForNewsEn) {
        this.subscribedForNewsEn = subscribedForNewsEn;
    }

    public boolean isSubscribedForNewsRu() {
        return subscribedForNewsRu;
    }

    public void setSubscribedForNewsRu(boolean subscribedForNewsRu) {
        this.subscribedForNewsRu = subscribedForNewsRu;
    }

    public int getBitField() {
        return bitField;
    }

    public void setBitField(int bitField) {
        this.bitField = bitField;
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
    public List<String> getLevelAbilities() {
        return levelAbilities;
    }

    public void setLevelAbilities(@Nullable List<String> levelAbilities) {
        this.levelAbilities = levelAbilities;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public List<Integer> getSocialAccounts() {
        return socialAccounts;
    }

    public void setSocialAccounts(List<Integer> socialAccounts) {
        this.socialAccounts = socialAccounts;
    }

    public List<Integer> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<Integer> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    @Nullable
    public String getInviteUrl() {
        return inviteUrl;
    }

    public void setInviteUrl(@Nullable String inviteUrl) {
        this.inviteUrl = inviteUrl;
    }

    @Nullable
    public String getTelegramBotUrl() {
        return telegramBotUrl;
    }

    public void setTelegramBotUrl(@Nullable String telegramBotUrl) {
        this.telegramBotUrl = telegramBotUrl;
    }

    @Nullable
    public String getBalance() {
        return balance;
    }

    public void setBalance(@Nullable String balance) {
        this.balance = balance;
    }

    @Nullable
    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(@Nullable String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }
}
