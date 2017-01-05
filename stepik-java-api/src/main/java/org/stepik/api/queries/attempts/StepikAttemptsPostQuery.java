package org.stepik.api.queries.attempts;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.attempts.Attempts;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikAttemptsPostQuery extends StepikAbstractPostQuery<Attempts> {
    private int step;

    public StepikAttemptsPostQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Attempts.class);
    }

    public StepikAttemptsPostQuery step(int id) {
        step = id;
        return this;
    }

    @Override
    protected String getBody() {
        return "{\"step\" = " + step + "}";
    }

    @Override
    protected String getUrl() {
        return Urls.ATTEMPTS;
    }
}
