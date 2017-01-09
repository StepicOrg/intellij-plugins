package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikExamSessionsAction extends StepikAbstractAction {
    public StepikExamSessionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
