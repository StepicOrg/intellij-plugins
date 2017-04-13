package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.ObjectsContainer;
import org.stepik.api.queries.reviews.StepikReviewSessionsPostQuery;

/**
 * @author meanmail
 */
public class StepikReviewSessionsAction extends StepikAbstractAction {
    public StepikReviewSessionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public <R extends ObjectsContainer> StepikReviewSessionsPostQuery post() {
        return new StepikReviewSessionsPostQuery(this);
    }
}
