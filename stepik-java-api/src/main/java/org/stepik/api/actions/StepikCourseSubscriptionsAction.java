package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCourseSubscriptionsAction extends StepikAbstractAction {
    public StepikCourseSubscriptionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
