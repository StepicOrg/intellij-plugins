package org.stepik.api.queries.attempts;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.attempts.Attempts;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikAttemptsGetQuery extends StepikAbstractGetQuery<StepikAttemptsGetQuery, Attempts> {
    public StepikAttemptsGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Attempts.class);
    }

    public StepikAttemptsGetQuery step(long id) {
        addParam("step", id);
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.ATTEMPTS;
    }
}
