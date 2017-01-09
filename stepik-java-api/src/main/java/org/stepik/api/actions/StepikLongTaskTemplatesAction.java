package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikLongTaskTemplatesAction extends StepikAbstractAction {
    public StepikLongTaskTemplatesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
