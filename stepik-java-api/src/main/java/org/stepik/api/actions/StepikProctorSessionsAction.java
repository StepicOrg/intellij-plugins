package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikProctorSessionsAction extends StepikAbstractAction {
    public StepikProctorSessionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
