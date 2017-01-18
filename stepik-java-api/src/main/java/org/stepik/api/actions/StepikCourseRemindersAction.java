package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCourseRemindersAction extends StepikAbstractAction {
    public StepikCourseRemindersAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
