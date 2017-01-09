package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCourseTotalStatisticsAction extends StepikAbstractAction {
    public StepikCourseTotalStatisticsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
