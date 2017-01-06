package org.stepik.api.queries.attempts;

import com.google.gson.Gson;
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

    public StepikAttemptsPostQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Attempts.class);
    }

    public StepikAttemptsPostQuery step(int id) {
        attempts.getAttempt().setStep(id);
        return this;
    }

    @Override
    protected String getBody() {
        return new Gson().toJson(attempts);
    }

    @Override
    protected String getUrl() {
        return Urls.ATTEMPTS;
    }
}
