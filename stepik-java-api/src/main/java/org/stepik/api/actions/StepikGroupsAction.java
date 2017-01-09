package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikGroupsAction extends StepikAbstractAction {
    public StepikGroupsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
