package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikRubricScoresAction extends StepikAbstractAction {
    public StepikRubricScoresAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
