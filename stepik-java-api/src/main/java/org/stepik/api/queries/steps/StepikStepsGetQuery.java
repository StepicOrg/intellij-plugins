package org.stepik.api.queries.steps;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.steps.Steps;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikStepsGetQuery extends StepikAbstractGetQuery<StepikStepsGetQuery, Steps> {
    public StepikStepsGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Steps.class);
    }

    @NotNull
    public StepikStepsGetQuery page(int value) {
        addParam("page", value);
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.STEPS;
    }
}
