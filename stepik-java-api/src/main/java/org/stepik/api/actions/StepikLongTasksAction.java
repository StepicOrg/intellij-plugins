package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikLongTasksAction extends StepikAbstractAction {
    public StepikLongTasksAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
