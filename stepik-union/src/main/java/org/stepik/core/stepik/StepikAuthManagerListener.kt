package org.stepik.core.stepik

interface StepikAuthManagerListener {
    fun stateChanged(oldState: StepikAuthState, newState: StepikAuthState)
}
