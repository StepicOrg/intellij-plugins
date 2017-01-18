package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikFavoriteCoursesAction extends StepikAbstractAction {
    public StepikFavoriteCoursesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
