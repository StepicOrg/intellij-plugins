package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.StepikCoursesGetQuery;

/**
 * @author meanmail
 */
public class StepikCoursesAction extends StepikBaseAction {

    public StepikCoursesAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikCoursesGetQuery get() {
        return new StepikCoursesGetQuery(this);
    }
}
