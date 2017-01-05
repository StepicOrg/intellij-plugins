package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikAbstractAction {
    private final StepikApiClient stepikApiClient;

    public StepikAbstractAction(StepikApiClient stepikApiClient) {
        this.stepikApiClient = stepikApiClient;
    }

    public StepikApiClient getStepikApiClient() {
        return stepikApiClient;
    }
}
