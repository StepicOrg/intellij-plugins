package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.steps.StepikStepsQuery;

/**
 * @author meanmail
 */
public class StepikStepsAction extends StepikAbstractAction {
    public StepikStepsAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikStepsQuery get() {
        return new StepikStepsQuery(this);
    }
}
