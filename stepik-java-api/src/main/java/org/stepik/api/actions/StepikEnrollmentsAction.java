package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.enrollments.StepikEnrollmentsGetQuery;
import org.stepik.api.queries.enrollments.StepikEnrollmentsPostQuery;

/**
 * @author meanmail
 */
public class StepikEnrollmentsAction extends StepikAbstractAction {
    public StepikEnrollmentsAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikEnrollmentsGetQuery get() {
        return new StepikEnrollmentsGetQuery(this);
    }

    public StepikEnrollmentsPostQuery post(){
        return new StepikEnrollmentsPostQuery(this);
    }
}
