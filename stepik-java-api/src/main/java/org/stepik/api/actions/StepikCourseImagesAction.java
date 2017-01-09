package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCourseImagesAction extends StepikAbstractAction {
    public StepikCourseImagesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
