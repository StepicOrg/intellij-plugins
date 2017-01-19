package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikInvitesAction extends StepikAbstractAction {
    public StepikInvitesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
