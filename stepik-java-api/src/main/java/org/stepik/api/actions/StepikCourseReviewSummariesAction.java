package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCourseReviewSummariesAction extends StepikAbstractAction {
    public StepikCourseReviewSummariesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
