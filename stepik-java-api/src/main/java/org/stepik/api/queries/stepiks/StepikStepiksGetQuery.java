package org.stepik.api.queries.stepiks;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.stepiks.Stepiks;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikStepiksGetQuery extends StepikAbstractGetQuery<StepikStepiksGetQuery, Stepiks> {
    private int id;

    public StepikStepiksGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Stepiks.class);
    }

    @NotNull
    public StepikStepiksGetQuery id(int value) {
        id = value;
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.STEPIKS + "/" + id;
    }
}
