package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCoursePeriodStatisticsAction extends StepikAbstractAction {
    public StepikCoursePeriodStatisticsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
