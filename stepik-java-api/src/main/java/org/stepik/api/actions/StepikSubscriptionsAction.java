package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikSubscriptionsAction extends StepikAbstractAction {
    public StepikSubscriptionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
