package org.stepik.api.objects.stepiks;

import com.google.gson.annotations.SerializedName;

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
}
