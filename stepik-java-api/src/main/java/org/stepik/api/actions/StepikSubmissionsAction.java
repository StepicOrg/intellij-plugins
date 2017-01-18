package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.submissions.StepikSubmissionsGetQuery;
import org.stepik.api.queries.submissions.StepikSubmissionsPostQuery;

/**
 * @author meanmail
 */
public class StepikSubmissionsAction extends StepikAbstractAction {
    public StepikSubmissionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikSubmissionsGetQuery get() {
        return new StepikSubmissionsGetQuery(this);
    }

    @NotNull
    public StepikSubmissionsPostQuery post() {
        return new StepikSubmissionsPostQuery(this);
    }
}
