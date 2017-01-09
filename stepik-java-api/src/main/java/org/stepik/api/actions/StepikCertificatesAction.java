package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikCertificatesAction extends StepikAbstractAction {
    public StepikCertificatesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
