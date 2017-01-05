package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.users.StepikUsersGetQuery;

/**
 * @author meanmail
 */
public class StepikUsersAction extends StepikAbstractAction {
    public StepikUsersAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikUsersGetQuery get() {
        return new StepikUsersGetQuery(this);
    }
}
