package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikSearchResultsAction extends StepikAbstractAction {
    public StepikSearchResultsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
