package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikMembersAction extends StepikAbstractAction {
    public StepikMembersAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
