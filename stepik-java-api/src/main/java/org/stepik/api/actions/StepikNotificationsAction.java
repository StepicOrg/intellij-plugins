package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikNotificationsAction extends StepikAbstractAction {
    public StepikNotificationsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
