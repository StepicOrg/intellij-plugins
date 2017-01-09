package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;

/**
 * @author meanmail
 */
public class StepikLicensesAction extends StepikAbstractAction {
    public StepikLicensesAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }
}
