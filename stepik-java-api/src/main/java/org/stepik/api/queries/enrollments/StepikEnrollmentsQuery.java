package org.stepik.api.queries.enrollments;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.enrollments.Enrollments;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikEnrollmentsQuery extends StepikAbstractGetQuery<Enrollments> {
    public StepikEnrollmentsQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Enrollments.class);
    }

    public StepikEnrollmentsQuery id(Integer... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikEnrollmentsQuery page(int value) {
        addParam("page", value);
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.ENROLLMENTS;
    }
}
