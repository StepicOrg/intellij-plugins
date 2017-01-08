package org.stepik.api.queries.steps;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.steps.Steps;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

import java.util.List;

/**
 * @author meanmail
 */
public class StepikStepsQuery extends StepikAbstractGetQuery<Steps> {
    public StepikStepsQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Steps.class);
    }

    public StepikStepsQuery id(Integer... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikStepsQuery id(int... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikStepsQuery id(List<Integer> values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikStepsQuery page(int value) {
        addParam("page", value);
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.STEPS;
    }
}
