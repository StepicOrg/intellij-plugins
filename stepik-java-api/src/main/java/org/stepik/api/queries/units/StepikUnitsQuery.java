package org.stepik.api.queries.units;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.units.Units;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikUnitsQuery extends StepikAbstractGetQuery<Units> {
    public StepikUnitsQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Units.class);
    }

    public StepikUnitsQuery id(Integer... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikUnitsQuery id(int[] values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikUnitsQuery id(int value) {
        addParam("ids[]", value);
        return this;
    }

    public StepikUnitsQuery page(int value) {
        addParam("page", value);
        return this;
    }

    public StepikUnitsQuery lesson(int value) {
        addParam("lesson", value);
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.UNITS;
    }
}
