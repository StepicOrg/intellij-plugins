package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikAnnouncementsAction extends StepikAbstractAction {
    public StepikAnnouncementsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}