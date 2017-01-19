package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikProfilesAction extends StepikAbstractAction {
    public StepikProfilesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
