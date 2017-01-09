package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikStripeSubscriptionsAction extends StepikAbstractAction {
    public StepikStripeSubscriptionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
