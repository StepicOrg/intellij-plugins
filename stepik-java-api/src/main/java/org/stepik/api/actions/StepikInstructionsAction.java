package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.instructions.StepikInstructionsGetQuery;

/**
 * @author meanmail
 */
public class StepikInstructionsAction extends StepikAbstractAction {
    public StepikInstructionsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikInstructionsGetQuery get() {
        return new StepikInstructionsGetQuery(this);
    }
}
