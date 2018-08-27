package org.hyperskill.api.client

import org.hyperskill.api.actions.HSLessonsAction
import org.hyperskill.api.actions.HSUsersAction
import org.stepik.api.client.StepikApiClient

fun StepikApiClient.hsUsers(): HSUsersAction {
    return HSUsersAction(this)
}

fun StepikApiClient.hsLessons(): HSLessonsAction {
    return HSLessonsAction(this)
}
