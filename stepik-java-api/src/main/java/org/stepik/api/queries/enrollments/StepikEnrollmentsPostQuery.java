package org.stepik.api.queries.enrollments;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikEnrollmentsPostQuery extends StepikAbstractPostQuery<String> {
    private int courseId;

    public StepikEnrollmentsPostQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, String.class);
    }

    public StepikEnrollmentsPostQuery course(int id) {
        courseId = id;
        return this;
    }

    @Override
    protected String getBody() {
        return "{\"enrollment\": {\"course\": " + courseId + "}}";
    }

    @Override
    protected String getUrl() {
        return Urls.ENROLLMENTS;
    }
}
