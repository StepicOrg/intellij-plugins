package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikShortUrlsAction extends StepikAbstractAction {
    public StepikShortUrlsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
