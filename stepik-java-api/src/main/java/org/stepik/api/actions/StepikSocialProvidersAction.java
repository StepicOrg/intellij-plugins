package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikSocialProvidersAction extends StepikAbstractAction {
    public StepikSocialProvidersAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
