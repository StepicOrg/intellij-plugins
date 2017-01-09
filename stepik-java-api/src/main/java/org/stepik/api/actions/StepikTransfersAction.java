package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikTransfersAction extends StepikAbstractAction {
    public StepikTransfersAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
