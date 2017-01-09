package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.courses.StepikCoursesGetQuery;

/**
 * @author meanmail
 */
public class StepikCoursesAction extends StepikAbstractAction {

    public StepikCoursesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikCoursesGetQuery get() {
        return new StepikCoursesGetQuery(this);
    }
}
