package org.hyperskill.api.client

import org.hyperskill.api.actions.HSAttemptsAction
import org.hyperskill.api.actions.HSLessonsAction
import org.hyperskill.api.actions.HSSubmissionsAction
import org.hyperskill.api.actions.HSUsersAction
import org.stepik.api.client.StepikApiClient

fun StepikApiClient.hsUsers(): HSUsersAction {
    return HSUsersAction(this)
}

fun StepikApiClient.hsLessons(): HSLessonsAction {
    return HSLessonsAction(this)
}

fun StepikApiClient.hsAttempts(): HSAttemptsAction {
    return HSAttemptsAction(this)
}

fun StepikApiClient.hsSubmissions(): HSSubmissionsAction {
    return HSSubmissionsAction(this)
}
