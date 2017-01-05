package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.enrollments.StepikEnrollmentsQuery;

/**
 * @author meanmail
 */
public class StepikEnrollmentsAction extends StepikAbstractAction {
    public StepikEnrollmentsAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikEnrollmentsQuery get() {
        return new StepikEnrollmentsQuery(this);
    }
}
