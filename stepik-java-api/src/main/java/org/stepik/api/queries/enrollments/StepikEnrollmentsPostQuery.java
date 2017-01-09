package org.stepik.api.queries.enrollments;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.enrollments.Enrollments;
import org.stepik.api.objects.enrollments.EnrollmentsPost;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikEnrollmentsPostQuery extends StepikAbstractPostQuery<Enrollments> {
    private final EnrollmentsPost enrollment = new EnrollmentsPost();

    public StepikEnrollmentsPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Enrollments.class);
    }

    @NotNull
    public StepikEnrollmentsPostQuery course(int id) {
        enrollment.getEnrollment().setCourse(id);
        return this;
    }

    @NotNull
    @Override
    protected String getBody() {
        return getJsonConverter().toJson(enrollment);
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.ENROLLMENTS;
    }
}
