package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikRemindersAction extends StepikAbstractAction {
    public StepikRemindersAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
