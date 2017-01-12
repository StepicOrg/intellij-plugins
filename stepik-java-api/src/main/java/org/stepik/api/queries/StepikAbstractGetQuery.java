package org.stepik.api.queries;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;

import java.util.List;

/**
 * @author meanmail
 */
public abstract class StepikAbstractGetQuery<T extends StepikAbstractGetQuery, R> extends StepikAbstractQuery<R> {
    protected StepikAbstractGetQuery(@NotNull StepikAbstractAction stepikAction, @NotNull Class<R> responseClass) {
        super(stepikAction, responseClass, QueryMethod.GET);
    }

    @NotNull
    public T id(@NotNull long... values) {
        addParam("ids[]", values);
        //noinspection unchecked
        return (T) this;
    }

    @NotNull
    public T id(@NotNull List<Long> values) {
        addParam("ids[]", values);
        //noinspection unchecked
        return (T) this;
    }
}
