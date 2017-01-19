package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCitiesAction extends StepikAbstractAction {
    public StepikCitiesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
