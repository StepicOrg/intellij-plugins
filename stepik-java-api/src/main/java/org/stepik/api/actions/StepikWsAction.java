package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikWsAction extends StepikAbstractAction {
    public StepikWsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
