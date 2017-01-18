package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikUserActivitiesAction extends StepikAbstractAction {
    public StepikUserActivitiesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
