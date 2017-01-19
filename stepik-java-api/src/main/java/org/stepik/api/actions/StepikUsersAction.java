package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.users.StepikUsersGetQuery;

/**
 * @author meanmail
 */
public class StepikUsersAction extends StepikAbstractAction {
    public StepikUsersAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikUsersGetQuery get() {
        return new StepikUsersGetQuery(this);
    }
}
