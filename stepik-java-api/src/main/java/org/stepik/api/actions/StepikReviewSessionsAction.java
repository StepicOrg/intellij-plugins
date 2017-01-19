package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikReviewSessionsAction extends StepikAbstractAction {
    public StepikReviewSessionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
