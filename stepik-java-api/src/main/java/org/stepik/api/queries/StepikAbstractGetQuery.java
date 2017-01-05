package org.stepik.api.queries;

import org.stepik.api.actions.StepikAbstractAction;

/**
 * @author meanmail
 */
public abstract class StepikAbstractGetQuery<T> extends StepikAbstractQuery<T> {
    protected StepikAbstractGetQuery(StepikAbstractAction stepikAction, Class<T> responseClass) {
        super(stepikAction, responseClass, QueryMethod.GET);
    }
}
