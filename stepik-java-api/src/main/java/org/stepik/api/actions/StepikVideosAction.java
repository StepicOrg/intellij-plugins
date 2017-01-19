package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikVideosAction extends StepikAbstractAction {
    public StepikVideosAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
