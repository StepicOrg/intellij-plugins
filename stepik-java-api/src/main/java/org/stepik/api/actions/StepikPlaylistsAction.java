package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikPlaylistsAction extends StepikAbstractAction {
    public StepikPlaylistsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
