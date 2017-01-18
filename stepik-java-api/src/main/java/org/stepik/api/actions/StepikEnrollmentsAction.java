package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.enrollments.StepikEnrollmentsGetQuery;
import org.stepik.api.queries.enrollments.StepikEnrollmentsPostQuery;

/**
 * @author meanmail
 */
public class StepikEnrollmentsAction extends StepikAbstractAction {
    public StepikEnrollmentsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikEnrollmentsGetQuery get() {
        return new StepikEnrollmentsGetQuery(this);
    }

    @NotNull
    public StepikEnrollmentsPostQuery post() {
        return new StepikEnrollmentsPostQuery(this);
    }
}
