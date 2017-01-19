package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikEmailTemplatesAction extends StepikAbstractAction {
    public StepikEmailTemplatesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
