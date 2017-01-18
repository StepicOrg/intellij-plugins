package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikVideoStatsAction extends StepikAbstractAction {
    public StepikVideoStatsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
