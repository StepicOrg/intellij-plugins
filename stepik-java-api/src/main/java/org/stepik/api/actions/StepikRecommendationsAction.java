package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikRecommendationsAction extends StepikAbstractAction {
    public StepikRecommendationsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
