package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikScriptsAction extends StepikAbstractAction {
    public StepikScriptsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
