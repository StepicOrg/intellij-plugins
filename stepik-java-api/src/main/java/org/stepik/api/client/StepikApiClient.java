package org.stepik.api.client;

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
import org.stepik.api.actions.StepikStepicsAction;
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
import org.stepik.api.auth.OAuth;

/**
 * @author meanmail
 */
public class StepikApiClient {
    private static final String VERSION = "1.0";
    private final TransportClient transportClient;

    public StepikApiClient(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    public OAuth oauth() {
        return new OAuth(this);
    }

    public StepikAnnouncementsAction announcements() {
        return new StepikAnnouncementsAction(this);
    }

    public StepikAssignmentsAction assignments() {
        return new StepikAssignmentsAction(this);

    }

    public StepikAttachmentsAction attachments() {
        return new StepikAttachmentsAction(this);

    }

    public StepikAttemptsAction attempts() {
        return new StepikAttemptsAction(this);

    }

    public StepikCertificatesAction certificates() {
        return new StepikCertificatesAction(this);
    }

    public StepikCitiesAction cities() {
        return new StepikCitiesAction(this);
    }

    public StepikCommentsAction comments() {
        return new StepikCommentsAction(this);
    }

    public StepikCountriesAction countries() {
        return new StepikCountriesAction(this);
    }

    public StepikCourseGradeBookCsvsAction courseGradeBookCsvs() {
        return new StepikCourseGradeBookCsvsAction(this);
    }

    public StepikCourseGradesAction courseGrades() {
        return new StepikCourseGradesAction(this);
    }

    public StepikCourseImagesAction courseImages() {
        return new StepikCourseImagesAction(this);
    }

    public StepikCoursePeriodStatisticsAction coursePeriodStatistics() {
        return new StepikCoursePeriodStatisticsAction(this);
    }

    public StepikCourseRemindersAction courseReminders() {
        return new StepikCourseRemindersAction(this);
    }

    public StepikCourseReviewSummariesAction courseReviewSummaries() {
        return new StepikCourseReviewSummariesAction(this);
    }

    public StepikCourseReviewsAction courseReviews() {
        return new StepikCourseReviewsAction(this);
    }

    public StepikCourseSubscriptionsAction courseSubscriptions() {
        return new StepikCourseSubscriptionsAction(this);
    }

    public StepikCourseTotalStatisticsAction courseTotalStatistics() {
        return new StepikCourseTotalStatisticsAction(this);
    }

    public StepikCoursesAction courses() {
        return new StepikCoursesAction(this);
    }

    public StepikDevicesAction devices() {
        return new StepikDevicesAction(this);
    }

    public StepikDiscussionProxiesAction discussionProxies() {
        return new StepikDiscussionProxiesAction(this);
    }

    public StepikDiscussionThreadsAction discussionThreads() {
        return new StepikDiscussionThreadsAction(this);
    }

    public StepikEmailAddressesAction emailAddresses() {
        return new StepikEmailAddressesAction(this);
    }

    public StepikEmailTemplatesAction emailTemplates() {
        return new StepikEmailTemplatesAction(this);
    }

    public StepikEnrollmentsAction enrollments() {
        return new StepikEnrollmentsAction(this);
    }

    public StepikEventsAction events() {
        return new StepikEventsAction(this);
    }

    public StepikExamSessionsAction examSessions() {
        return new StepikExamSessionsAction(this);
    }

    public StepikFavoriteCoursesAction favoriteCourses() {
        return new StepikFavoriteCoursesAction(this);
    }

    public StepikGroupsAction groups() {
        return new StepikGroupsAction(this);
    }

    public StepikInstructionsAction instructions() {
        return new StepikInstructionsAction(this);
    }

    public StepikInvitesAction invites() {
        return new StepikInvitesAction(this);
    }

    public StepikLastStepsAction lastSteps() {
        return new StepikLastStepsAction(this);
    }

    public StepikLessonImagesAction lessonImages() {
        return new StepikLessonImagesAction(this);
    }

    public StepikLessonsAction lessons() {
        return new StepikLessonsAction(this);
    }

    public StepikLicensesAction licenses() {
        return new StepikLicensesAction(this);
    }

    public StepikLongTaskTemplatesAction longTaskTemplates() {
        return new StepikLongTaskTemplatesAction(this);
    }

    public StepikLongTasksAction longTasks() {
        return new StepikLongTasksAction(this);
    }

    public StepikMembersAction members() {
        return new StepikMembersAction(this);
    }

    public StepikMetricsAction metrics() {
        return new StepikMetricsAction(this);
    }

    public StepikNotificationStatusesAction notificationStatuses() {
        return new StepikNotificationStatusesAction(this);
    }

    public StepikNotificationsAction notifications() {
        return new StepikNotificationsAction(this);
    }

    public StepikPaymentsAction payments() {
        return new StepikPaymentsAction(this);
    }

    public StepikPlaylistsAction playlists() {
        return new StepikPlaylistsAction(this);
    }

    public StepikProctorSessionsAction proctorSessions() {
        return new StepikProctorSessionsAction(this);
    }

    public StepikProfileImagesAction profileImages() {
        return new StepikProfileImagesAction(this);
    }

    public StepikProfilesAction profiles() {
        return new StepikProfilesAction(this);
    }

    public StepikProgressesAction progresses() {
        return new StepikProgressesAction(this);
    }

    public StepikRecommendationReactionsAction recommendationReactions() {
        return new StepikRecommendationReactionsAction(this);
    }

    public StepikRecommendationsAction recommendations() {
        return new StepikRecommendationsAction(this);
    }

    public StepikRegionsAction regions() {
        return new StepikRegionsAction(this);
    }

    public StepikRemindersAction reminders() {
        return new StepikRemindersAction(this);
    }

    public StepikReviewSessionsAction reviewSessions() {
        return new StepikReviewSessionsAction(this);
    }

    public StepikReviewsAction reviews() {
        return new StepikReviewsAction(this);
    }

    public StepikRubricsAction rubrics() {
        return new StepikRubricsAction(this);
    }

    public StepikScoreFilesAction scoreFiles() {
        return new StepikScoreFilesAction(this);
    }

    public StepikScriptsAction scripts() {
        return new StepikScriptsAction(this);
    }

    public StepikSearchResultsAction searchResults() {
        return new StepikSearchResultsAction(this);
    }

    public StepikSectionsAction sections() {
        return new StepikSectionsAction(this);
    }

    public StepikShortUrlsAction shortUrls() {
        return new StepikShortUrlsAction(this);
    }

    public StepikSocialAccountsAction socialAccounts() {
        return new StepikSocialAccountsAction(this);
    }

    public StepikSocialProfilesAction socialProfiles() {
        return new StepikSocialProfilesAction(this);
    }

    public StepikSocialProvidersAction socialProviders() {
        return new StepikSocialProvidersAction(this);
    }

    public StepikStatusServicesAction statusServices() {
        return new StepikStatusServicesAction(this);
    }

    public StepikStepIssuesAction stepIssues() {
        return new StepikStepIssuesAction(this);
    }

    public StepikStepSnapshotsAction stepSnapshots() {
        return new StepikStepSnapshotsAction(this);
    }

    public StepikStepSourcesAction stepSources() {
        return new StepikStepSourcesAction(this);
    }

    public StepikStepicsAction stepics() {
        return new StepikStepicsAction(this);
    }

    public StepikStepsAction steps() {
        return new StepikStepsAction(this);
    }

    public StepikStripeSubscriptionsAction stripeSubscriptions() {
        return new StepikStripeSubscriptionsAction(this);
    }

    public StepikSubmissionsAction submissions() {
        return new StepikSubmissionsAction(this);
    }

    public StepikSubscriptionsAction subscriptions() {
        return new StepikSubscriptionsAction(this);
    }

    public StepikTagProgressesAction tagProgresses() {
        return new StepikTagProgressesAction(this);
    }

    public StepikTagSuggestionsAction tagSuggestions() {
        return new StepikTagSuggestionsAction(this);
    }

    public StepikTagsAction tags() {
        return new StepikTagsAction(this);
    }

    public StepikTopLessonsAction topLessons() {
        return new StepikTopLessonsAction(this);
    }

    public StepikTransfersAction transfers() {
        return new StepikTransfersAction(this);
    }

    public StepikUnitsAction units() {
        return new StepikUnitsAction(this);
    }

    public StepikUserActivitiesAction userActivities() {
        return new StepikUserActivitiesAction(this);
    }

    public StepikUsersAction users() {
        return new StepikUsersAction(this);
    }

    public StepikVideoStatsAction videoStats() {
        return new StepikVideoStatsAction(this);
    }

    public StepikVideosAction videos() {
        return new StepikVideosAction(this);
    }

    public StepikViewsAction views() {
        return new StepikViewsAction(this);
    }

    public StepikVotesAction votes() {
        return new StepikVotesAction(this);
    }

    public StepikWsAction ws() {
        return new StepikWsAction(this);
    }

    public String getVersion() {
        return VERSION;
    }
}
