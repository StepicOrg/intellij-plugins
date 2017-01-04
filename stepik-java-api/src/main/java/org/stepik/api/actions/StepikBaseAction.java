package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikBaseAction {
    private final StepikApiClient stepikApiClient;

    public StepikBaseAction(StepikApiClient stepikApiClient) {
        this.stepikApiClient = stepikApiClient;
    }
}
