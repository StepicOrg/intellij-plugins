package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikRegionsAction extends StepikAbstractAction {
    public StepikRegionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
