package org.stepik.api.objects.stepiks;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class Config {
    @SerializedName("index_text")
    private String indexText;
    @SerializedName("can_change_name")
    private boolean canChangeName;
    @SerializedName("footer_logo")
    private String footerLogo;
    @SerializedName("project_name")
    private String projectName;
    @SerializedName("can_change_city")
    private boolean canChangeCity;
    @SerializedName("project_help_center_url")
    private String projectHelpCenterUrl;
    @SerializedName("can_change_language")
    private boolean canChangeLanguage;
    @SerializedName("has_extra_favicons")
    private boolean hasExtraFavicons;
    @SerializedName("project_main_instance_url")
    private String projectMainInstanceUrl;
    @SerializedName("has_email_verification_alert")
    private boolean hasEmailVerificationAlert;
    @SerializedName("can_change_email")
    private boolean canChangeEmail;
    private String favicon;
    @SerializedName("has_course_review_tab")
    private boolean hasCourseReviewTab;
    @SerializedName("course_reviews_passed_percent")
    private int courseReviewsPassedPercent;
    @SerializedName("is_public_telegram_bot")
    private boolean isPublicTelegramBot;
    @SerializedName("has_lessons_in_navbar")
    private boolean hasLessonsInNavbar;
    @SerializedName("is_standard_index")
    private boolean isStandardIndex;
    @SerializedName("courses_default_publicity")
    private boolean coursesDefaultPublicity;
    @SerializedName("telegram_bot_name")
    private String telegramBotName;
    @SerializedName("has_mobile_apps_banner")
    private boolean hasMobileAppsBanner;
    @SerializedName("payments_yandex_money_shop_id")
    private String paymentsYandexMoneyShopId;
    @SerializedName("is_full_profile")
    private boolean isFullProfile;
    @SerializedName("topbar_logo")
    private String topbarLogo;
    private String language;
    @SerializedName("payments_yandex_money_form_action")
    private String paymentsYandexMoneyFormAction;
    @SerializedName("has_activity_graph")
    private boolean hasActivityGraph;
    @SerializedName("is_standard_explore")
    private boolean isStandardExplore;
    @SerializedName("has_registration_link")
    private boolean hasRegistrationLink;
    @SerializedName("is_public_social_accounts")
    private boolean isPublicSocialAccounts;
    @SerializedName("project_domain")
    private String projectDomain;
    @SerializedName("is_standard_footer")
    private boolean isStandardFooter;
    @SerializedName("index_logo")
    private String indexLogo;
    @SerializedName("has_language_selector")
    private boolean hasLanguageSelector;
    @SerializedName("can_set_password")
    private boolean canSetPassword;
    @SerializedName("stripe_api_public_key")
    private String stripeApiPublicKey;
    @SerializedName("payments_yandex_money_scid")
    private String paymentsYandexMoneyScid;
    @SerializedName("login_link")
    private String loginLink;

    @Nullable
    public String getIndexText() {
        return indexText;
    }

    public void setIndexText(@Nullable String indexText) {
        this.indexText = indexText;
    }

    public boolean isCanChangeName() {
        return canChangeName;
    }

    public void setCanChangeName(boolean canChangeName) {
        this.canChangeName = canChangeName;
    }

    @Nullable
    public String getFooterLogo() {
        return footerLogo;
    }

    public void setFooterLogo(@Nullable String footerLogo) {
        this.footerLogo = footerLogo;
    }

    @Nullable
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(@Nullable String projectName) {
        this.projectName = projectName;
    }

    public boolean isCanChangeCity() {
        return canChangeCity;
    }

    public void setCanChangeCity(boolean canChangeCity) {
        this.canChangeCity = canChangeCity;
    }

    @Nullable
    public String getProjectHelpCenterUrl() {
        return projectHelpCenterUrl;
    }

    public void setProjectHelpCenterUrl(@Nullable String projectHelpCenterUrl) {
        this.projectHelpCenterUrl = projectHelpCenterUrl;
    }

    public boolean isCanChangeLanguage() {
        return canChangeLanguage;
    }

    public void setCanChangeLanguage(boolean canChangeLanguage) {
        this.canChangeLanguage = canChangeLanguage;
    }

    public boolean isHasExtraFavicons() {
        return hasExtraFavicons;
    }

    public void setHasExtraFavicons(boolean hasExtraFavicons) {
        this.hasExtraFavicons = hasExtraFavicons;
    }

    @Nullable
    public String getProjectMainInstanceUrl() {
        return projectMainInstanceUrl;
    }

    public void setProjectMainInstanceUrl(@Nullable String projectMainInstanceUrl) {
        this.projectMainInstanceUrl = projectMainInstanceUrl;
    }

    public boolean isHasEmailVerificationAlert() {
        return hasEmailVerificationAlert;
    }

    public void setHasEmailVerificationAlert(boolean hasEmailVerificationAlert) {
        this.hasEmailVerificationAlert = hasEmailVerificationAlert;
    }

    public boolean isCanChangeEmail() {
        return canChangeEmail;
    }

    public void setCanChangeEmail(boolean canChangeEmail) {
        this.canChangeEmail = canChangeEmail;
    }

    @Nullable
    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(@Nullable String favicon) {
        this.favicon = favicon;
    }

    public boolean isHasCourseReviewTab() {
        return hasCourseReviewTab;
    }

    public void setHasCourseReviewTab(boolean hasCourseReviewTab) {
        this.hasCourseReviewTab = hasCourseReviewTab;
    }

    public int getCourseReviewsPassedPercent() {
        return courseReviewsPassedPercent;
    }

    public void setCourseReviewsPassedPercent(int courseReviewsPassedPercent) {
        this.courseReviewsPassedPercent = courseReviewsPassedPercent;
    }

    public boolean isPublicTelegramBot() {
        return isPublicTelegramBot;
    }

    public void setPublicTelegramBot(boolean publicTelegramBot) {
        isPublicTelegramBot = publicTelegramBot;
    }

    public boolean isHasLessonsInNavbar() {
        return hasLessonsInNavbar;
    }

    public void setHasLessonsInNavbar(boolean hasLessonsInNavbar) {
        this.hasLessonsInNavbar = hasLessonsInNavbar;
    }

    public boolean isStandardIndex() {
        return isStandardIndex;
    }

    public void setStandardIndex(boolean standardIndex) {
        isStandardIndex = standardIndex;
    }

    public boolean isCoursesDefaultPublicity() {
        return coursesDefaultPublicity;
    }

    public void setCoursesDefaultPublicity(boolean coursesDefaultPublicity) {
        this.coursesDefaultPublicity = coursesDefaultPublicity;
    }

    @Nullable
    public String getTelegramBotName() {
        return telegramBotName;
    }

    public void setTelegramBotName(@Nullable String telegramBotName) {
        this.telegramBotName = telegramBotName;
    }

    public boolean isHasMobileAppsBanner() {
        return hasMobileAppsBanner;
    }

    public void setHasMobileAppsBanner(boolean hasMobileAppsBanner) {
        this.hasMobileAppsBanner = hasMobileAppsBanner;
    }

    @Nullable
    public String getPaymentsYandexMoneyShopId() {
        return paymentsYandexMoneyShopId;
    }

    public void setPaymentsYandexMoneyShopId(@Nullable String paymentsYandexMoneyShopId) {
        this.paymentsYandexMoneyShopId = paymentsYandexMoneyShopId;
    }

    public boolean isFullProfile() {
        return isFullProfile;
    }

    public void setFullProfile(boolean fullProfile) {
        isFullProfile = fullProfile;
    }

    @Nullable
    public String getTopbarLogo() {
        return topbarLogo;
    }

    public void setTopbarLogo(@Nullable String topbarLogo) {
        this.topbarLogo = topbarLogo;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    public void setLanguage(@Nullable String language) {
        this.language = language;
    }

    @Nullable
    public String getPaymentsYandexMoneyFormAction() {
        return paymentsYandexMoneyFormAction;
    }

    public void setPaymentsYandexMoneyFormAction(@Nullable String paymentsYandexMoneyFormAction) {
        this.paymentsYandexMoneyFormAction = paymentsYandexMoneyFormAction;
    }

    public boolean isHasActivityGraph() {
        return hasActivityGraph;
    }

    public void setHasActivityGraph(boolean hasActivityGraph) {
        this.hasActivityGraph = hasActivityGraph;
    }

    public boolean isStandardExplore() {
        return isStandardExplore;
    }

    public void setStandardExplore(boolean standardExplore) {
        isStandardExplore = standardExplore;
    }

    public boolean isHasRegistrationLink() {
        return hasRegistrationLink;
    }

    public void setHasRegistrationLink(boolean hasRegistrationLink) {
        this.hasRegistrationLink = hasRegistrationLink;
    }

    public boolean isPublicSocialAccounts() {
        return isPublicSocialAccounts;
    }

    public void setPublicSocialAccounts(boolean publicSocialAccounts) {
        isPublicSocialAccounts = publicSocialAccounts;
    }

    @Nullable
    public String getProjectDomain() {
        return projectDomain;
    }

    public void setProjectDomain(@Nullable String projectDomain) {
        this.projectDomain = projectDomain;
    }

    public boolean isStandardFooter() {
        return isStandardFooter;
    }

    public void setStandardFooter(boolean standardFooter) {
        isStandardFooter = standardFooter;
    }

    @Nullable
    public String getIndexLogo() {
        return indexLogo;
    }

    public void setIndexLogo(@Nullable String indexLogo) {
        this.indexLogo = indexLogo;
    }

    public boolean isHasLanguageSelector() {
        return hasLanguageSelector;
    }

    public void setHasLanguageSelector(boolean hasLanguageSelector) {
        this.hasLanguageSelector = hasLanguageSelector;
    }

    public boolean isCanSetPassword() {
        return canSetPassword;
    }

    public void setCanSetPassword(boolean canSetPassword) {
        this.canSetPassword = canSetPassword;
    }

    @Nullable
    public String getStripeApiPublicKey() {
        return stripeApiPublicKey;
    }

    public void setStripeApiPublicKey(@Nullable String stripeApiPublicKey) {
        this.stripeApiPublicKey = stripeApiPublicKey;
    }

    @Nullable
    public String getPaymentsYandexMoneyScid() {
        return paymentsYandexMoneyScid;
    }

    public void setPaymentsYandexMoneyScid(@Nullable String paymentsYandexMoneyScid) {
        this.paymentsYandexMoneyScid = paymentsYandexMoneyScid;
    }

    @Nullable
    public String getLoginLink() {
        return loginLink;
    }

    public void setLoginLink(@Nullable String loginLink) {
        this.loginLink = loginLink;
    }
}
