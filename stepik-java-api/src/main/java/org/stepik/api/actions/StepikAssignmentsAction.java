package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikAssignmentsAction extends StepikAbstractAction {
    public StepikAssignmentsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
