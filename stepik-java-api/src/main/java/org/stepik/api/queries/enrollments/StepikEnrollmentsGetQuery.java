package org.stepik.api.queries.enrollments;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.enrollments.Enrollments;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikEnrollmentsGetQuery extends StepikAbstractGetQuery<StepikEnrollmentsGetQuery, Enrollments> {
    public StepikEnrollmentsGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Enrollments.class);
    }

    @NotNull
    public StepikEnrollmentsGetQuery page(int value) {
        addParam("page", value);
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.ENROLLMENTS;
    }
}
