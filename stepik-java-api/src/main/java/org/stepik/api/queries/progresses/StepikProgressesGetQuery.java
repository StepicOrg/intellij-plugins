package org.stepik.api.queries.progresses;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.progresses.Progresses;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikProgressesGetQuery extends StepikAbstractGetQuery<StepikProgressesGetQuery, Progresses> {
    public StepikProgressesGetQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Progresses.class);
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.PROGRESSES;
    }

    @Override
    protected boolean isCacheEnabled() {
        return false;
    }

    @NotNull
    public StepikProgressesGetQuery page(int page) {
        addParam("page", page);
        return this;
    }
}
