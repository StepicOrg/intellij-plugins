package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.ObjectsContainer;
import org.stepik.api.queries.attempts.StepikAttemptsGetQuery;
import org.stepik.api.queries.attempts.StepikAttemptsPostQuery;

/**
 * @author meanmail
 */
public class StepikAttemptsAction extends StepikAbstractAction {

    public StepikAttemptsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public <R extends ObjectsContainer> StepikAttemptsPostQuery post() {
        return new StepikAttemptsPostQuery(this);
    }

    public <R extends ObjectsContainer> StepikAttemptsGetQuery get() {
        return new StepikAttemptsGetQuery(this);
    }
}
