package org.stepik.api.queries.units;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.units.Units;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

import java.util.List;

/**
 * @author meanmail
 */
public class StepikUnitsGetQuery extends StepikAbstractGetQuery<StepikUnitsGetQuery, Units> {
    public StepikUnitsGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Units.class);
    }

    @NotNull
    public StepikUnitsGetQuery page(int value) {
        addParam("page", value);
        return this;
    }

    @NotNull
    public StepikUnitsGetQuery page(@NotNull List<Integer> value) {
        addParam("page", value);
        return this;
    }

    @NotNull
    public StepikUnitsGetQuery lesson(int value) {
        addParam("lesson", value);
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.UNITS;
    }
}
