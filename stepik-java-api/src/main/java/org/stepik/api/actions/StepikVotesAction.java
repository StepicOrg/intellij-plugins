package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikVotesAction extends StepikAbstractAction {
    public StepikVotesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}