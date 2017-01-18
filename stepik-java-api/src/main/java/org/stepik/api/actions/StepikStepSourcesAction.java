package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikStepSourcesAction extends StepikAbstractAction {
    public StepikStepSourcesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
