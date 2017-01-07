package org.stepik.api.queries.enrollments;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.enrollments.EnrollmentsPost;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikEnrollmentsPostQuery extends StepikAbstractPostQuery<String> {
    private final EnrollmentsPost enrollment = new EnrollmentsPost();

    public StepikEnrollmentsPostQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, String.class);
    }

    public StepikEnrollmentsPostQuery course(int id) {
        enrollment.getEnrollment().setCourse(id);
        return this;
    }

    @Override
    protected String getBody() {
        return getJsonConverter().toJson(enrollment);
    }

    @Override
    protected String getUrl() {
        return Urls.ENROLLMENTS;
    }
}
