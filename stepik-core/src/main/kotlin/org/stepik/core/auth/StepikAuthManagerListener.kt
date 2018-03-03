package org.stepik.core.auth

interface StepikAuthManagerListener {
    fun stateChanged(oldState: StepikAuthState, newState: StepikAuthState)
}
