package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.recommendations.StepikRecommendationsGetQuery;

/**
 * @author meanmail
 */
public class StepikRecommendationsAction extends StepikAbstractAction {
    public StepikRecommendationsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikRecommendationsGetQuery get() {
        return new StepikRecommendationsGetQuery(this);
    }
}
