package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikStepIssuesAction extends StepikAbstractAction {
    public StepikStepIssuesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
