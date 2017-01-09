package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikLessonImagesAction extends StepikAbstractAction {
    public StepikLessonImagesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
