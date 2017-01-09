package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikSocialAccountsAction extends StepikAbstractAction {
    public StepikSocialAccountsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
