package org.stepik.api.queries;

import org.stepik.api.actions.StepikBaseAction;

/**
 * @author meanmail
 */
public class StepikBaseQuery {
    private final StepikBaseAction stepikBaseAction;

    public StepikBaseQuery(StepikBaseAction stepikBaseAction) {
        this.stepikBaseAction = stepikBaseAction;
    }
}
