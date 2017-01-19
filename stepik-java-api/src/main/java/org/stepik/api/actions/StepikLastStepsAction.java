package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikLastStepsAction extends StepikAbstractAction {
    public StepikLastStepsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
