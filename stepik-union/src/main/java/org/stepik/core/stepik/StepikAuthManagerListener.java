package org.stepik.core.stepik;

import org.jetbrains.annotations.NotNull;

public interface StepikAuthManagerListener {
    void stateChanged(@NotNull StepikAuthState oldState, @NotNull StepikAuthState newState);
}
