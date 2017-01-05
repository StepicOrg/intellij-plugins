package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.stepiks.StepikStepiksGetQuery;

/**
 * @author meanmail
 */
public class StepikStepiksAction extends StepikAbstractAction {
    public StepikStepiksAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikStepiksGetQuery get() {
        return new StepikStepiksGetQuery(this);
    }
}
