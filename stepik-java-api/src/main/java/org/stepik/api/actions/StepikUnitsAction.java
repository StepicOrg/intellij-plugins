package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.units.StepikUnitsGetQuery;

/**
 * @author meanmail
 */
public class StepikUnitsAction extends StepikAbstractAction {
    public StepikUnitsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikUnitsGetQuery get() {
        return new StepikUnitsGetQuery(this);
    }
}
