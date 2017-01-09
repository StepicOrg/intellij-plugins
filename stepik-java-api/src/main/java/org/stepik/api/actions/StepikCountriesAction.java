package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCountriesAction extends StepikAbstractAction {
    public StepikCountriesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
