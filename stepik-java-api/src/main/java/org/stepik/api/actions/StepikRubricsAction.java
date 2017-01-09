package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikRubricsAction extends StepikAbstractAction {
    public StepikRubricsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
