package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikSocialProfilesAction extends StepikAbstractAction {
    public StepikSocialProfilesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
