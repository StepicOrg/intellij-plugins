package org.stepik.api.queries.enrollments;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.enrollments.Enrollments;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikEnrollmentsGetQuery extends StepikAbstractGetQuery<Enrollments> {
    public StepikEnrollmentsGetQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Enrollments.class);
    }

    public StepikEnrollmentsGetQuery id(Integer... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikEnrollmentsGetQuery page(int value) {
        addParam("page", value);
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.ENROLLMENTS;
    }
}
