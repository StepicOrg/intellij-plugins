package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.views.StepikViewsPostQuery;

/**
 * @author meanmail
 */
public class StepikViewsAction extends StepikAbstractAction {
    public StepikViewsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikViewsPostQuery post() {
        return new StepikViewsPostQuery(this);
    }

}
