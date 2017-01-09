package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikStepSnapshotsAction extends StepikAbstractAction {
    public StepikStepSnapshotsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
