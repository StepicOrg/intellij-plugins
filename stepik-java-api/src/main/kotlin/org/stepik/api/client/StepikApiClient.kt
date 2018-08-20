package org.stepik.api.client

import org.stepik.api.actions.StepikAnnouncementsAction
import org.stepik.api.actions.StepikAssignmentsAction
import org.stepik.api.actions.StepikAttachmentsAction
import org.stepik.api.actions.StepikAttemptsAction
import org.stepik.api.actions.StepikCertificatesAction
import org.stepik.api.actions.StepikCitiesAction
import org.stepik.api.actions.StepikCommentsAction
import org.stepik.api.actions.StepikCountriesAction
import org.stepik.api.actions.StepikCourseGradeBookCsvsAction
import org.stepik.api.actions.StepikCourseGradesAction
import org.stepik.api.actions.StepikCourseImagesAction
import org.stepik.api.actions.StepikCoursePeriodStatisticsAction
import org.stepik.api.actions.StepikCourseRemindersAction
import org.stepik.api.actions.StepikCourseReviewSummariesAction
import org.stepik.api.actions.StepikCourseReviewsAction
import org.stepik.api.actions.StepikCourseSubscriptionsAction
import org.stepik.api.actions.StepikCourseTotalStatisticsAction
import org.stepik.api.actions.StepikCoursesAction
import org.stepik.api.actions.StepikDevicesAction
import org.stepik.api.actions.StepikDiscussionProxiesAction
import org.stepik.api.actions.StepikDiscussionThreadsAction
import org.stepik.api.actions.StepikEmailAddressesAction
import org.stepik.api.actions.StepikEmailTemplatesAction
import org.stepik.api.actions.StepikEnrollmentsAction
import org.stepik.api.actions.StepikEventsAction
import org.stepik.api.actions.StepikExamSessionsAction
import org.stepik.api.actions.StepikFavoriteCoursesAction
import org.stepik.api.actions.StepikFilesAction
import org.stepik.api.actions.StepikGroupsAction
import org.stepik.api.actions.StepikInstructionsAction
import org.stepik.api.actions.StepikInvitesAction
import org.stepik.api.actions.StepikLastStepsAction
import org.stepik.api.actions.StepikLessonImagesAction
import org.stepik.api.actions.StepikLessonsAction
import org.stepik.api.actions.StepikLicensesAction
import org.stepik.api.actions.StepikLongTaskTemplatesAction
import org.stepik.api.actions.StepikLongTasksAction
import org.stepik.api.actions.StepikMembersAction
import org.stepik.api.actions.StepikMetricsAction
import org.stepik.api.actions.StepikNotificationStatusesAction
import org.stepik.api.actions.StepikNotificationsAction
import org.stepik.api.actions.StepikPaymentsAction
import org.stepik.api.actions.StepikPlaylistsAction
import org.stepik.api.actions.StepikProctorSessionsAction
import org.stepik.api.actions.StepikProfileImagesAction
import org.stepik.api.actions.StepikProfilesAction
import org.stepik.api.actions.StepikProgressesAction
import org.stepik.api.actions.StepikRecommendationReactionsAction
import org.stepik.api.actions.StepikRecommendationsAction
import org.stepik.api.actions.StepikRegionsAction
import org.stepik.api.actions.StepikRemindersAction
import org.stepik.api.actions.StepikReviewSessionsAction
import org.stepik.api.actions.StepikReviewsAction
import org.stepik.api.actions.StepikRubricScoresAction
import org.stepik.api.actions.StepikRubricsAction
import org.stepik.api.actions.StepikScoreFilesAction
import org.stepik.api.actions.StepikScriptsAction
import org.stepik.api.actions.StepikSearchResultsAction
import org.stepik.api.actions.StepikSectionsAction
import org.stepik.api.actions.StepikShortUrlsAction
import org.stepik.api.actions.StepikSocialAccountsAction
import org.stepik.api.actions.StepikSocialProfilesAction
import org.stepik.api.actions.StepikSocialProvidersAction
import org.stepik.api.actions.StepikStatusServicesAction
import org.stepik.api.actions.StepikStepIssuesAction
import org.stepik.api.actions.StepikStepSnapshotsAction
import org.stepik.api.actions.StepikStepSourcesAction
import org.stepik.api.actions.StepikStepiksAction
import org.stepik.api.actions.StepikStepsAction
import org.stepik.api.actions.StepikStripeSubscriptionsAction
import org.stepik.api.actions.StepikSubmissionsAction
import org.stepik.api.actions.StepikSubscriptionsAction
import org.stepik.api.actions.StepikTagProgressesAction
import org.stepik.api.actions.StepikTagSuggestionsAction
import org.stepik.api.actions.StepikTagsAction
import org.stepik.api.actions.StepikTopLessonsAction
import org.stepik.api.actions.StepikTransfersAction
import org.stepik.api.actions.StepikUnitsAction
import org.stepik.api.actions.StepikUserActivitiesAction
import org.stepik.api.actions.StepikUsersAction
import org.stepik.api.actions.StepikVideoStatsAction
import org.stepik.api.actions.StepikVideosAction
import org.stepik.api.actions.StepikViewsAction
import org.stepik.api.actions.StepikVotesAction
import org.stepik.api.actions.StepikWsAction
import org.stepik.api.auth.OAuth2
import org.stepik.api.client.serialization.DefaultJsonConverter
import org.stepik.api.client.serialization.JsonConverter
import org.stepik.api.objects.auth.TokenInfo
import java.nio.file.Paths

class StepikApiClient(transportClient: HttpTransportClient, val host: String) {
    
    val transportClient: TransportClient
    val jsonConverter: JsonConverter
    @Volatile
    @set:Synchronized
    var tokenInfo: TokenInfo? = null
        @Synchronized get() {
            if (field == null) {
                this.tokenInfo = TokenInfo()
            }
            return field
        }
    var cachePath = Paths.get(System.getProperty("user.home"), ".stepik", "stepik-api", "cache")
    var isCacheEnabled = true
    
    constructor(userAgent: String, host: String) : this(HttpTransportClient.getInstance(userAgent), host)
    
    init {
        this.transportClient = transportClient
        this.jsonConverter = DefaultJsonConverter
    }
    
    fun oauth2(): OAuth2 {
        return OAuth2(this)
    }
    
    fun announcements(): StepikAnnouncementsAction {
        return StepikAnnouncementsAction(this)
    }
    
    fun assignments(): StepikAssignmentsAction {
        return StepikAssignmentsAction(this)
    }
    
    fun attachments(): StepikAttachmentsAction {
        return StepikAttachmentsAction(this)
    }
    
    fun attempts(): StepikAttemptsAction {
        return StepikAttemptsAction(this)
    }
    
    fun certificates(): StepikCertificatesAction {
        return StepikCertificatesAction(this)
    }
    
    fun cities(): StepikCitiesAction {
        return StepikCitiesAction(this)
    }
    
    fun comments(): StepikCommentsAction {
        return StepikCommentsAction(this)
    }
    
    fun countries(): StepikCountriesAction {
        return StepikCountriesAction(this)
    }
    
    fun courseGradeBookCsvs(): StepikCourseGradeBookCsvsAction {
        return StepikCourseGradeBookCsvsAction(this)
    }
    
    fun courseGrades(): StepikCourseGradesAction {
        return StepikCourseGradesAction(this)
    }
    
    fun courseImages(): StepikCourseImagesAction {
        return StepikCourseImagesAction(this)
    }
    
    fun coursePeriodStatistics(): StepikCoursePeriodStatisticsAction {
        return StepikCoursePeriodStatisticsAction(this)
    }
    
    fun courseReminders(): StepikCourseRemindersAction {
        return StepikCourseRemindersAction(this)
    }
    
    fun courseReviewSummaries(): StepikCourseReviewSummariesAction {
        return StepikCourseReviewSummariesAction(this)
    }
    
    fun courseReviews(): StepikCourseReviewsAction {
        return StepikCourseReviewsAction(this)
    }
    
    fun courseSubscriptions(): StepikCourseSubscriptionsAction {
        return StepikCourseSubscriptionsAction(this)
    }
    
    fun courseTotalStatistics(): StepikCourseTotalStatisticsAction {
        return StepikCourseTotalStatisticsAction(this)
    }
    
    fun courses(): StepikCoursesAction {
        return StepikCoursesAction(this)
    }
    
    fun devices(): StepikDevicesAction {
        return StepikDevicesAction(this)
    }
    
    fun discussionProxies(): StepikDiscussionProxiesAction {
        return StepikDiscussionProxiesAction(this)
    }
    
    fun discussionThreads(): StepikDiscussionThreadsAction {
        return StepikDiscussionThreadsAction(this)
    }
    
    fun emailAddresses(): StepikEmailAddressesAction {
        return StepikEmailAddressesAction(this)
    }
    
    fun emailTemplates(): StepikEmailTemplatesAction {
        return StepikEmailTemplatesAction(this)
    }
    
    fun enrollments(): StepikEnrollmentsAction {
        return StepikEnrollmentsAction(this)
    }
    
    fun events(): StepikEventsAction {
        return StepikEventsAction(this)
    }
    
    fun examSessions(): StepikExamSessionsAction {
        return StepikExamSessionsAction(this)
    }
    
    fun favoriteCourses(): StepikFavoriteCoursesAction {
        return StepikFavoriteCoursesAction(this)
    }
    
    fun files(): StepikFilesAction {
        return StepikFilesAction(this)
    }
    
    fun groups(): StepikGroupsAction {
        return StepikGroupsAction(this)
    }
    
    fun instructions(): StepikInstructionsAction {
        return StepikInstructionsAction(this)
    }
    
    fun invites(): StepikInvitesAction {
        return StepikInvitesAction(this)
    }
    
    fun lastSteps(): StepikLastStepsAction {
        return StepikLastStepsAction(this)
    }
    
    fun lessonImages(): StepikLessonImagesAction {
        return StepikLessonImagesAction(this)
    }
    
    fun lessons(): StepikLessonsAction {
        return StepikLessonsAction(this)
    }
    
    fun licenses(): StepikLicensesAction {
        return StepikLicensesAction(this)
    }
    
    fun longTaskTemplates(): StepikLongTaskTemplatesAction {
        return StepikLongTaskTemplatesAction(this)
    }
    
    fun longTasks(): StepikLongTasksAction {
        return StepikLongTasksAction(this)
    }
    
    fun members(): StepikMembersAction {
        return StepikMembersAction(this)
    }
    
    fun metrics(): StepikMetricsAction {
        return StepikMetricsAction(this)
    }
    
    fun notificationStatuses(): StepikNotificationStatusesAction {
        return StepikNotificationStatusesAction(this)
    }
    
    fun notifications(): StepikNotificationsAction {
        return StepikNotificationsAction(this)
    }
    
    fun payments(): StepikPaymentsAction {
        return StepikPaymentsAction(this)
    }
    
    fun playlists(): StepikPlaylistsAction {
        return StepikPlaylistsAction(this)
    }
    
    fun proctorSessions(): StepikProctorSessionsAction {
        return StepikProctorSessionsAction(this)
    }
    
    fun profileImages(): StepikProfileImagesAction {
        return StepikProfileImagesAction(this)
    }
    
    fun profiles(): StepikProfilesAction {
        return StepikProfilesAction(this)
    }
    
    fun progresses(): StepikProgressesAction {
        return StepikProgressesAction(this)
    }
    
    fun recommendationReactions(): StepikRecommendationReactionsAction {
        return StepikRecommendationReactionsAction(this)
    }
    
    fun recommendations(): StepikRecommendationsAction {
        return StepikRecommendationsAction(this)
    }
    
    fun regions(): StepikRegionsAction {
        return StepikRegionsAction(this)
    }
    
    fun reminders(): StepikRemindersAction {
        return StepikRemindersAction(this)
    }
    
    fun reviewSessions(): StepikReviewSessionsAction {
        return StepikReviewSessionsAction(this)
    }
    
    fun reviews(): StepikReviewsAction {
        return StepikReviewsAction(this)
    }
    
    fun rubrics(): StepikRubricsAction {
        return StepikRubricsAction(this)
    }
    
    fun rubricScores(): StepikRubricScoresAction {
        return StepikRubricScoresAction(this)
    }
    
    fun scoreFiles(): StepikScoreFilesAction {
        return StepikScoreFilesAction(this)
    }
    
    fun scripts(): StepikScriptsAction {
        return StepikScriptsAction(this)
    }
    
    fun searchResults(): StepikSearchResultsAction {
        return StepikSearchResultsAction(this)
    }
    
    fun sections(): StepikSectionsAction {
        return StepikSectionsAction(this)
    }
    
    fun shortUrls(): StepikShortUrlsAction {
        return StepikShortUrlsAction(this)
    }
    
    fun socialAccounts(): StepikSocialAccountsAction {
        return StepikSocialAccountsAction(this)
    }
    
    fun socialProfiles(): StepikSocialProfilesAction {
        return StepikSocialProfilesAction(this)
    }
    
    fun socialProviders(): StepikSocialProvidersAction {
        return StepikSocialProvidersAction(this)
    }
    
    fun statusServices(): StepikStatusServicesAction {
        return StepikStatusServicesAction(this)
    }
    
    fun stepIssues(): StepikStepIssuesAction {
        return StepikStepIssuesAction(this)
    }
    
    fun stepSnapshots(): StepikStepSnapshotsAction {
        return StepikStepSnapshotsAction(this)
    }
    
    fun stepSources(): StepikStepSourcesAction {
        return StepikStepSourcesAction(this)
    }
    
    fun stepiks(): StepikStepiksAction {
        return StepikStepiksAction(this)
    }
    
    fun steps(): StepikStepsAction {
        return StepikStepsAction(this)
    }
    
    fun stripeSubscriptions(): StepikStripeSubscriptionsAction {
        return StepikStripeSubscriptionsAction(this)
    }
    
    fun submissions(): StepikSubmissionsAction {
        return StepikSubmissionsAction(this)
    }
    
    fun subscriptions(): StepikSubscriptionsAction {
        return StepikSubscriptionsAction(this)
    }
    
    fun tagProgresses(): StepikTagProgressesAction {
        return StepikTagProgressesAction(this)
    }
    
    fun tagSuggestions(): StepikTagSuggestionsAction {
        return StepikTagSuggestionsAction(this)
    }
    
    fun tags(): StepikTagsAction {
        return StepikTagsAction(this)
    }
    
    fun topLessons(): StepikTopLessonsAction {
        return StepikTopLessonsAction(this)
    }
    
    fun transfers(): StepikTransfersAction {
        return StepikTransfersAction(this)
    }
    
    fun units(): StepikUnitsAction {
        return StepikUnitsAction(this)
    }
    
    fun userActivities(): StepikUserActivitiesAction {
        return StepikUserActivitiesAction(this)
    }
    
    fun users(): StepikUsersAction {
        return StepikUsersAction(this)
    }
    
    fun videoStats(): StepikVideoStatsAction {
        return StepikVideoStatsAction(this)
    }
    
    fun videos(): StepikVideosAction {
        return StepikVideosAction(this)
    }
    
    fun views(): StepikViewsAction {
        return StepikViewsAction(this)
    }
    
    fun votes(): StepikVotesAction {
        return StepikVotesAction(this)
    }
    
    fun ws(): StepikWsAction {
        return StepikWsAction(this)
    }
    
    companion object {
        const val version = "0.2"
    }
}
