package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.units.StepikUnitsQuery;

/**
 * @author meanmail
 */
public class StepikUnitsAction extends StepikAbstractAction {
    public StepikUnitsAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikUnitsQuery get() {
        return new StepikUnitsQuery(this);
    }
}
