package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.progresses.StepikProgressesGetQuery;

/**
 * @author meanmail
 */
public class StepikProgressesAction extends StepikAbstractAction {
    public StepikProgressesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikProgressesGetQuery get() {
        return new StepikProgressesGetQuery(this);
    }
}
