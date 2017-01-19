package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikTagsAction extends StepikAbstractAction {
    public StepikTagsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
