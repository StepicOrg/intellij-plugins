package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikReviewsAction extends StepikAbstractAction {
    public StepikReviewsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
