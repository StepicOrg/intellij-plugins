package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikNotificationStatusesAction extends StepikAbstractAction {
    public StepikNotificationStatusesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}