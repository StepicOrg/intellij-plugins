package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.metrics.StepikMetricsPostQuery;

/**
 * @author meanmail
 */
public class StepikMetricsAction extends StepikAbstractAction {
    public StepikMetricsAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikMetricsPostQuery post() {
        return new StepikMetricsPostQuery(this);
    }
}
