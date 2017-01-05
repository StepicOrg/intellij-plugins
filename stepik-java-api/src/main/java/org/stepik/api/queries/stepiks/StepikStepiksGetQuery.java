package org.stepik.api.queries.stepiks;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.stepiks.Stepiks;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikStepiksGetQuery extends StepikAbstractGetQuery<Stepiks>{
    private int id;

    public StepikStepiksGetQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Stepiks.class);
    }

    public StepikStepiksGetQuery id(int value) {
        id = value;
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.STEPIKS + "/" + id;
    }
}
