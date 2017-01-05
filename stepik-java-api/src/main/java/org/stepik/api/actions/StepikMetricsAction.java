package org.stepik.api.actions;

import org.stepik.api.client.StepikApiClient;
import org.stepik.api.queries.metrics.StepikMetricsPostQuery;

/**
 * @author meanmail
 */
public class StepikMetricsAction extends StepikAbstractAction {
    public StepikMetricsAction(StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    public StepikMetricsPostQuery post() {
        return new StepikMetricsPostQuery(this);
    }
}
