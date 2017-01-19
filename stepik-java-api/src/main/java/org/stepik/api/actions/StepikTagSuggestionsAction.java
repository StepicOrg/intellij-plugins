package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikTagSuggestionsAction extends StepikAbstractAction {
    public StepikTagSuggestionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
