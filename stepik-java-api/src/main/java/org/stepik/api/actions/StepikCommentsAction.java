package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCommentsAction extends StepikAbstractAction {
    public StepikCommentsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
