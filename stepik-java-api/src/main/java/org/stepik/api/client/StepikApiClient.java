package org.stepik.api.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.actions.StepikAnnouncementsAction;
import org.stepik.api.actions.StepikAssignmentsAction;
import org.stepik.api.actions.StepikAttachmentsAction;
import org.stepik.api.actions.StepikAttemptsAction;
import org.stepik.api.actions.StepikCertificatesAction;
import org.stepik.api.actions.StepikCitiesAction;
import org.stepik.api.actions.StepikCommentsAction;
import org.stepik.api.actions.StepikCountriesAction;
import org.stepik.api.actions.StepikCourseGradeBookCsvsAction;
import org.stepik.api.actions.StepikCourseGradesAction;
import org.stepik.api.actions.StepikCourseImagesAction;
import org.stepik.api.actions.StepikCoursePeriodStatisticsAction;
import org.stepik.api.actions.StepikCourseRemindersAction;
import org.stepik.api.actions.StepikCourseReviewSummariesAction;
import org.stepik.api.actions.StepikCourseReviewsAction;
import org.stepik.api.actions.StepikCourseSubscriptionsAction;
import org.stepik.api.actions.StepikCourseTotalStatisticsAction;
import org.stepik.api.actions.StepikCoursesAction;
import org.stepik.api.actions.StepikDevicesAction;
import org.stepik.api.actions.StepikDiscussionProxiesAction;
import org.stepik.api.actions.StepikDiscussionThreadsAction;
import org.stepik.api.actions.StepikEmailAddressesAction;
import org.stepik.api.actions.StepikEmailTemplatesAction;
import org.stepik.api.actions.StepikEnrollmentsAction;
import org.stepik.api.actions.StepikEventsAction;
import org.stepik.api.actions.StepikExamSessionsAction;
import org.stepik.api.actions.StepikFavoriteCoursesAction;
import org.stepik.api.actions.StepikGroupsAction;
import org.stepik.api.actions.StepikInstructionsAction;
import org.stepik.api.actions.StepikInvitesAction;
import org.stepik.api.actions.StepikLastStepsAction;
import org.stepik.api.actions.StepikLessonImagesAction;
import org.stepik.api.actions.StepikLessonsAction;
import org.stepik.api.actions.StepikLicensesAction;
import org.stepik.api.actions.StepikLongTaskTemplatesAction;
import org.stepik.api.actions.StepikLongTasksAction;
import org.stepik.api.actions.StepikMembersAction;
import org.stepik.api.actions.StepikMetricsAction;
import org.stepik.api.actions.StepikNotificationStatusesAction;
import org.stepik.api.actions.StepikNotificationsAction;
import org.stepik.api.actions.StepikPaymentsAction;
import org.stepik.api.actions.StepikPlaylistsAction;
import org.stepik.api.actions.StepikProctorSessionsAction;
import org.stepik.api.actions.StepikProfileImagesAction;
import org.stepik.api.actions.StepikProfilesAction;
import org.stepik.api.actions.StepikProgressesAction;
import org.stepik.api.actions.StepikRecommendationReactionsAction;
import org.stepik.api.actions.StepikRecommendationsAction;
import org.stepik.api.actions.StepikRegionsAction;
import org.stepik.api.actions.StepikRemindersAction;
import org.stepik.api.actions.StepikReviewSessionsAction;
import org.stepik.api.actions.StepikReviewsAction;
import org.stepik.api.actions.StepikRubricScoresAction;
import org.stepik.api.actions.StepikRubricsAction;
import org.stepik.api.actions.StepikScoreFilesAction;
import org.stepik.api.actions.StepikScriptsAction;
import org.stepik.api.actions.StepikSearchResultsAction;
import org.stepik.api.actions.StepikSectionsAction;
import org.stepik.api.actions.StepikShortUrlsAction;
import org.stepik.api.actions.StepikSocialAccountsAction;
import org.stepik.api.actions.StepikSocialProfilesAction;
import org.stepik.api.actions.StepikSocialProvidersAction;
import org.stepik.api.actions.StepikStatusServicesAction;
import org.stepik.api.actions.StepikStepIssuesAction;
import org.stepik.api.actions.StepikStepSnapshotsAction;
import org.stepik.api.actions.StepikStepSourcesAction;
import org.stepik.api.actions.StepikStepiksAction;
import org.stepik.api.actions.StepikStepsAction;
import org.stepik.api.actions.StepikStripeSubscriptionsAction;
import org.stepik.api.actions.StepikSubmissionsAction;
import org.stepik.api.actions.StepikSubscriptionsAction;
import org.stepik.api.actions.StepikTagProgressesAction;
import org.stepik.api.actions.StepikTagSuggestionsAction;
import org.stepik.api.actions.StepikTagsAction;
import org.stepik.api.actions.StepikTopLessonsAction;
import org.stepik.api.actions.StepikTransfersAction;
import org.stepik.api.actions.StepikUnitsAction;
import org.stepik.api.actions.StepikUserActivitiesAction;
import org.stepik.api.actions.StepikUsersAction;
import org.stepik.api.actions.StepikVideoStatsAction;
import org.stepik.api.actions.StepikVideosAction;
import org.stepik.api.actions.StepikViewsAction;
import org.stepik.api.actions.StepikVotesAction;
import org.stepik.api.actions.StepikWsAction;
import org.stepik.api.auth.OAuth2;
import org.stepik.api.objects.auth.TokenInfo;

/**
 * @author meanmail
 */
public class StepikApiClient {
    private static final String VERSION = "0.1";
    private final TransportClient transportClient;
    private final JsonConverter jsonConverter;
    private TokenInfo tokenInfo;

    public StepikApiClient() {
        this(HttpTransportClient.getInstance());
    }

    public StepikApiClient(@NotNull HttpTransportClient transportClient) {
        this.transportClient = transportClient;
        this.jsonConverter = DefaultJsonConverter.getInstance();
    }

    @NotNull
    public static String getVersion() {
        return VERSION;
    }

    @NotNull
    public OAuth2 oauth2() {
        return new OAuth2(this);
    }

    @NotNull
    public StepikAnnouncementsAction announcements() {
        return new StepikAnnouncementsAction(this);
    }

    @NotNull
    public StepikAssignmentsAction assignments() {
        return new StepikAssignmentsAction(this);
    }

    @NotNull
    public StepikAttachmentsAction attachments() {
        return new StepikAttachmentsAction(this);
    }

    @NotNull
    public StepikAttemptsAction attempts() {
        return new StepikAttemptsAction(this);
    }

    @NotNull
    public StepikCertificatesAction certificates() {
        return new StepikCertificatesAction(this);
    }

    @NotNull
    public StepikCitiesAction cities() {
        return new StepikCitiesAction(this);
    }

    @NotNull
    public StepikCommentsAction comments() {
        return new StepikCommentsAction(this);
    }

    @NotNull
    public StepikCountriesAction countries() {
        return new StepikCountriesAction(this);
    }

    @NotNull
    public StepikCourseGradeBookCsvsAction courseGradeBookCsvs() {
        return new StepikCourseGradeBookCsvsAction(this);
    }

    @NotNull
    public StepikCourseGradesAction courseGrades() {
        return new StepikCourseGradesAction(this);
    }

    @NotNull
    public StepikCourseImagesAction courseImages() {
        return new StepikCourseImagesAction(this);
    }

    @NotNull
    public StepikCoursePeriodStatisticsAction coursePeriodStatistics() {
        return new StepikCoursePeriodStatisticsAction(this);
    }

    @NotNull
    public StepikCourseRemindersAction courseReminders() {
        return new StepikCourseRemindersAction(this);
    }

    @NotNull
    public StepikCourseReviewSummariesAction courseReviewSummaries() {
        return new StepikCourseReviewSummariesAction(this);
    }

    @NotNull
    public StepikCourseReviewsAction courseReviews() {
        return new StepikCourseReviewsAction(this);
    }

    @NotNull
    public StepikCourseSubscriptionsAction courseSubscriptions() {
        return new StepikCourseSubscriptionsAction(this);
    }

    @NotNull
    public StepikCourseTotalStatisticsAction courseTotalStatistics() {
        return new StepikCourseTotalStatisticsAction(this);
    }

    @NotNull
    public StepikCoursesAction courses() {
        return new StepikCoursesAction(this);
    }

    @NotNull
    public StepikDevicesAction devices() {
        return new StepikDevicesAction(this);
    }

    @NotNull
    public StepikDiscussionProxiesAction discussionProxies() {
        return new StepikDiscussionProxiesAction(this);
    }

    @NotNull
    public StepikDiscussionThreadsAction discussionThreads() {
        return new StepikDiscussionThreadsAction(this);
    }

    @NotNull
    public StepikEmailAddressesAction emailAddresses() {
        return new StepikEmailAddressesAction(this);
    }

    @NotNull
    public StepikEmailTemplatesAction emailTemplates() {
        return new StepikEmailTemplatesAction(this);
    }

    @NotNull
    public StepikEnrollmentsAction enrollments() {
        return new StepikEnrollmentsAction(this);
    }

    @NotNull
    public StepikEventsAction events() {
        return new StepikEventsAction(this);
    }

    @NotNull
    public StepikExamSessionsAction examSessions() {
        return new StepikExamSessionsAction(this);
    }

    @NotNull
    public StepikFavoriteCoursesAction favoriteCourses() {
        return new StepikFavoriteCoursesAction(this);
    }

    @NotNull
    public StepikGroupsAction groups() {
        return new StepikGroupsAction(this);
    }

    @NotNull
    public StepikInstructionsAction instructions() {
        return new StepikInstructionsAction(this);
    }

    @NotNull
    public StepikInvitesAction invites() {
        return new StepikInvitesAction(this);
    }

    @NotNull
    public StepikLastStepsAction lastSteps() {
        return new StepikLastStepsAction(this);
    }

    @NotNull
    public StepikLessonImagesAction lessonImages() {
        return new StepikLessonImagesAction(this);
    }

    @NotNull
    public StepikLessonsAction lessons() {
        return new StepikLessonsAction(this);
    }

    @NotNull
    public StepikLicensesAction licenses() {
        return new StepikLicensesAction(this);
    }

    @NotNull
    public StepikLongTaskTemplatesAction longTaskTemplates() {
        return new StepikLongTaskTemplatesAction(this);
    }

    @NotNull
    public StepikLongTasksAction longTasks() {
        return new StepikLongTasksAction(this);
    }

    @NotNull
    public StepikMembersAction members() {
        return new StepikMembersAction(this);
    }

    @NotNull
    public StepikMetricsAction metrics() {
        return new StepikMetricsAction(this);
    }

    @NotNull
    public StepikNotificationStatusesAction notificationStatuses() {
        return new StepikNotificationStatusesAction(this);
    }

    @NotNull
    public StepikNotificationsAction notifications() {
        return new StepikNotificationsAction(this);
    }

    @NotNull
    public StepikPaymentsAction payments() {
        return new StepikPaymentsAction(this);
    }

    @NotNull
    public StepikPlaylistsAction playlists() {
        return new StepikPlaylistsAction(this);
    }

    @NotNull
    public StepikProctorSessionsAction proctorSessions() {
        return new StepikProctorSessionsAction(this);
    }

    @NotNull
    public StepikProfileImagesAction profileImages() {
        return new StepikProfileImagesAction(this);
    }

    @NotNull
    public StepikProfilesAction profiles() {
        return new StepikProfilesAction(this);
    }

    @NotNull
    public StepikProgressesAction progresses() {
        return new StepikProgressesAction(this);
    }

    @NotNull
    public StepikRecommendationReactionsAction recommendationReactions() {
        return new StepikRecommendationReactionsAction(this);
    }

    @NotNull
    public StepikRecommendationsAction recommendations() {
        return new StepikRecommendationsAction(this);
    }

    @NotNull
    public StepikRegionsAction regions() {
        return new StepikRegionsAction(this);
    }

    @NotNull
    public StepikRemindersAction reminders() {
        return new StepikRemindersAction(this);
    }

    @NotNull
    public StepikReviewSessionsAction reviewSessions() {
        return new StepikReviewSessionsAction(this);
    }

    @NotNull
    public StepikReviewsAction reviews() {
        return new StepikReviewsAction(this);
    }

    @NotNull
    public StepikRubricsAction rubrics() {
        return new StepikRubricsAction(this);
    }

    @NotNull
    public StepikRubricScoresAction rubricScores() {
        return new StepikRubricScoresAction(this);
    }

    @NotNull
    public StepikScoreFilesAction scoreFiles() {
        return new StepikScoreFilesAction(this);
    }

    @NotNull
    public StepikScriptsAction scripts() {
        return new StepikScriptsAction(this);
    }

    @NotNull
    public StepikSearchResultsAction searchResults() {
        return new StepikSearchResultsAction(this);
    }

    @NotNull
    public StepikSectionsAction sections() {
        return new StepikSectionsAction(this);
    }

    @NotNull
    public StepikShortUrlsAction shortUrls() {
        return new StepikShortUrlsAction(this);
    }

    @NotNull
    public StepikSocialAccountsAction socialAccounts() {
        return new StepikSocialAccountsAction(this);
    }

    @NotNull
    public StepikSocialProfilesAction socialProfiles() {
        return new StepikSocialProfilesAction(this);
    }

    @NotNull
    public StepikSocialProvidersAction socialProviders() {
        return new StepikSocialProvidersAction(this);
    }

    @NotNull
    public StepikStatusServicesAction statusServices() {
        return new StepikStatusServicesAction(this);
    }

    @NotNull
    public StepikStepIssuesAction stepIssues() {
        return new StepikStepIssuesAction(this);
    }

    @NotNull
    public StepikStepSnapshotsAction stepSnapshots() {
        return new StepikStepSnapshotsAction(this);
    }

    @NotNull
    public StepikStepSourcesAction stepSources() {
        return new StepikStepSourcesAction(this);
    }

    @NotNull
    public StepikStepiksAction stepiks() {
        return new StepikStepiksAction(this);
    }

    @NotNull
    public StepikStepsAction steps() {
        return new StepikStepsAction(this);
    }

    @NotNull
    public StepikStripeSubscriptionsAction stripeSubscriptions() {
        return new StepikStripeSubscriptionsAction(this);
    }

    @NotNull
    public StepikSubmissionsAction submissions() {
        return new StepikSubmissionsAction(this);
    }

    @NotNull
    public StepikSubscriptionsAction subscriptions() {
        return new StepikSubscriptionsAction(this);
    }

    @NotNull
    public StepikTagProgressesAction tagProgresses() {
        return new StepikTagProgressesAction(this);
    }

    @NotNull
    public StepikTagSuggestionsAction tagSuggestions() {
        return new StepikTagSuggestionsAction(this);
    }

    @NotNull
    public StepikTagsAction tags() {
        return new StepikTagsAction(this);
    }

    @NotNull
    public StepikTopLessonsAction topLessons() {
        return new StepikTopLessonsAction(this);
    }

    @NotNull
    public StepikTransfersAction transfers() {
        return new StepikTransfersAction(this);
    }

    @NotNull
    public StepikUnitsAction units() {
        return new StepikUnitsAction(this);
    }

    @NotNull
    public StepikUserActivitiesAction userActivities() {
        return new StepikUserActivitiesAction(this);
    }

    @NotNull
    public StepikUsersAction users() {
        return new StepikUsersAction(this);
    }

    @NotNull
    public StepikVideoStatsAction videoStats() {
        return new StepikVideoStatsAction(this);
    }

    @NotNull
    public StepikVideosAction videos() {
        return new StepikVideosAction(this);
    }

    @NotNull
    public StepikViewsAction views() {
        return new StepikViewsAction(this);
    }

    @NotNull
    public StepikVotesAction votes() {
        return new StepikVotesAction(this);
    }

    @NotNull
    public StepikWsAction ws() {
        return new StepikWsAction(this);
    }

    @NotNull
    public TransportClient getTransportClient() {
        return transportClient;
    }

    @NotNull
    public TokenInfo getTokenInfo() {
        if (tokenInfo == null) {
            tokenInfo = new TokenInfo();
        }
        return tokenInfo;
    }

    public void setTokenInfo(@Nullable TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public void reset() {
        tokenInfo = null;
    }

    @NotNull
    public JsonConverter getJsonConverter() {
        return jsonConverter;
    }
}
