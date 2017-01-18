package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikEventsAction extends StepikAbstractAction {
    public StepikEventsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
