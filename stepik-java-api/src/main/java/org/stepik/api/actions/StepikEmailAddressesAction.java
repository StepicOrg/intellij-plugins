package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikEmailAddressesAction extends StepikAbstractAction {
    public StepikEmailAddressesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
