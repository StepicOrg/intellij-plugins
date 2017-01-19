package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikDevicesAction extends StepikAbstractAction {
    public StepikDevicesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
