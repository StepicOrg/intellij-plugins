package org.stepik.api.queries;

import org.stepik.api.actions.StepikAbstractAction;

/**
 * @author meanmail
 */
public abstract class StepikAbstractPostQuery<T> extends StepikAbstractQuery<T> {
    protected StepikAbstractPostQuery(StepikAbstractAction stepikAction, Class<T> responseClass) {
        super(stepikAction, responseClass, QueryMethod.POST);
    }
}
