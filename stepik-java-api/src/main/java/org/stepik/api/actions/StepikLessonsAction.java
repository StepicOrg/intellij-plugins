package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.lessons.StepikLessonsGetQuery;

/**
 * @author meanmail
 */
public class StepikLessonsAction extends StepikAbstractAction {
    public StepikLessonsAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikLessonsGetQuery get() {
        return new StepikLessonsGetQuery(this);
    }
}
