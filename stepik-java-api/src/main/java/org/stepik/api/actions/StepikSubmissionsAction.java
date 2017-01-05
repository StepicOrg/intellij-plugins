package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.submissions.StepikSubmissionsGetQuery;
import org.stepik.api.queries.submissions.StepikSubmissionsPostQuery;

/**
 * @author meanmail
 */
public class StepikSubmissionsAction extends StepikAbstractAction {
    public StepikSubmissionsAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikSubmissionsGetQuery get() {
        return new StepikSubmissionsGetQuery(this);
    }

    public StepikSubmissionsPostQuery post() {
        return new StepikSubmissionsPostQuery(this);
    }
}
