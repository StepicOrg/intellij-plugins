package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCourseReviewsAction extends StepikAbstractAction {
    public StepikCourseReviewsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
