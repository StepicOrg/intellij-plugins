package org.stepik.api.queries;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;

/**
 * @author meanmail
 */
public abstract class StepikAbstractPostQuery<T> extends StepikAbstractQuery<T> {
    protected StepikAbstractPostQuery(@NotNull StepikAbstractAction stepikAction, @NotNull Class<T> responseClass) {
        super(stepikAction, responseClass, QueryMethod.POST);
    }

    @NotNull
    protected String getContentType() {
        return "application/json";
    }
}
