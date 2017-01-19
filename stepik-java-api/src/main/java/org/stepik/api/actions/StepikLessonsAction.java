package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.lessons.StepikLessonsGetQuery;

/**
 * @author meanmail
 */
public class StepikLessonsAction extends StepikAbstractAction {
    public StepikLessonsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikLessonsGetQuery get() {
        return new StepikLessonsGetQuery(this);
    }
}
