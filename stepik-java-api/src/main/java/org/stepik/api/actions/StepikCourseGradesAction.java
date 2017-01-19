package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCourseGradesAction extends StepikAbstractAction {
    public StepikCourseGradesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
