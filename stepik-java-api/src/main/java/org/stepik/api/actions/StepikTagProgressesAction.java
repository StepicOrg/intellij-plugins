package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikTagProgressesAction extends StepikAbstractAction {
    public StepikTagProgressesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
