package org.stepik.api.client

import org.stepik.api.actions.HSUsersAction

fun StepikApiClient.hsUsers(): HSUsersAction {
    return HSUsersAction(this)
}
