package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.courses.StepikCoursesGetQuery;

/**
 * @author meanmail
 */
public class StepikCoursesAction extends StepikAbstractAction {

    public StepikCoursesAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikCoursesGetQuery get() {
        return new StepikCoursesGetQuery(this);
    }
}
