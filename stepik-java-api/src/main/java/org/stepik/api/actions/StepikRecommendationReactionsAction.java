package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.recommendations.StepikReactionsPostQuery;

/**
 * @author meanmail
 */
public class StepikRecommendationReactionsAction extends StepikAbstractAction {
    public StepikRecommendationReactionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikReactionsPostQuery post() {
        return new StepikReactionsPostQuery(this);
    }

}
