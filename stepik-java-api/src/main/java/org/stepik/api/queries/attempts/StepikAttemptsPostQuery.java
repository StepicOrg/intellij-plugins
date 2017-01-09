package org.stepik.api.queries.attempts;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.attempts.Attempts;
import org.stepik.api.objects.attempts.AttemptsPost;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikAttemptsPostQuery extends StepikAbstractPostQuery<Attempts> {
    private final AttemptsPost attempts = new AttemptsPost();

    public StepikAttemptsPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Attempts.class);
    }

    @NotNull
    public StepikAttemptsPostQuery step(int id) {
        attempts.getAttempt().setStep(id);
        return this;
    }

    @NotNull
    @Override
    protected String getBody() {
        return getJsonConverter().toJson(attempts);
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.ATTEMPTS;
    }
}
