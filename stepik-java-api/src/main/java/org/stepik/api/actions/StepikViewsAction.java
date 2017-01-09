package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikViewsAction extends StepikAbstractAction {
    public StepikViewsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
