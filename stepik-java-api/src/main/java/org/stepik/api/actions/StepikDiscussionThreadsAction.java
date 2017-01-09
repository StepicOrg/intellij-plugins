package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikDiscussionThreadsAction extends StepikAbstractAction {
    public StepikDiscussionThreadsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
