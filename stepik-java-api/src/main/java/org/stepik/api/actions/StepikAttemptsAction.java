package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.attempts.StepikAttemptsPostQuery;

/**
 * @author meanmail
 */
public class StepikAttemptsAction extends StepikAbstractAction {

    public StepikAttemptsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikAttemptsPostQuery post() {
        return new StepikAttemptsPostQuery(this);
    }
}
