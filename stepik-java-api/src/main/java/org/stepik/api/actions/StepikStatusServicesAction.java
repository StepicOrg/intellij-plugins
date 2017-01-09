package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikStatusServicesAction extends StepikAbstractAction {
    public StepikStatusServicesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
