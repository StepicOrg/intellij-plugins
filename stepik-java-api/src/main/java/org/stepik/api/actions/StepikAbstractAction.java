package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikAbstractAction {
    private final StepikApiClient stepikApiClient;

    public StepikAbstractAction(@NotNull StepikApiClient stepikApiClient) {
        this.stepikApiClient = stepikApiClient;
    }

    @NotNull
    public StepikApiClient getStepikApiClient() {
        return stepikApiClient;
    }
}
