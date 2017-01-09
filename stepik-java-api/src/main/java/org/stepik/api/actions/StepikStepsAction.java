package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.steps.StepikStepsGetQuery;

/**
 * @author meanmail
 */
public class StepikStepsAction extends StepikAbstractAction {
    public StepikStepsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikStepsGetQuery get() {
        return new StepikStepsGetQuery(this);
    }
}
