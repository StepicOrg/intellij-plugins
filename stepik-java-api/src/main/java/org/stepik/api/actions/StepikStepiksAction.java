package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.stepiks.StepikStepiksGetQuery;

/**
 * @author meanmail
 */
public class StepikStepiksAction extends StepikAbstractAction {
    public StepikStepiksAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikStepiksGetQuery get() {
        return new StepikStepiksGetQuery(this);
    }
}
