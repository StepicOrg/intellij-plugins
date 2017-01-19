package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikRecommendationReactionsAction extends StepikAbstractAction {
    public StepikRecommendationReactionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
