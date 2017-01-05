package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.attempts.StepikAttemptsPostQuery;

/**
 * @author meanmail
 */
public class StepikAttemptsAction extends StepikAbstractAction {

    public StepikAttemptsAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikAttemptsPostQuery post() {
        return new StepikAttemptsPostQuery(this);
    }
}
