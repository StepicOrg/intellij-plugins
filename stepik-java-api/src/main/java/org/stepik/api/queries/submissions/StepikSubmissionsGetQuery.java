package org.stepik.api.queries.submissions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.submissions.ReviewStatus;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.queries.Order;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikSubmissionsGetQuery extends StepikAbstractGetQuery<StepikSubmissionsGetQuery, Submissions> {
    public StepikSubmissionsGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Submissions.class);
    }

    @NotNull
    public StepikSubmissionsGetQuery status(@NotNull String value) {
        addParam("status", value);
        return this;
    }

    @NotNull
    public StepikSubmissionsGetQuery userName(@NotNull String value) {
        addParam("user_name", value);
        return this;
    }

    @NotNull
    public StepikSubmissionsGetQuery step(long value) {
        addParam("step", value);
        return this;
    }

    @NotNull
    public StepikSubmissionsGetQuery user(long value) {
        addParam("user", value);
        return this;
    }

    @NotNull
    public StepikSubmissionsGetQuery attempt(long value) {
        addParam("attempt", value);
        return this;
    }

    @NotNull
    public StepikSubmissionsGetQuery search(@NotNull String value) {
        addParam("search", value);
        return this;
    }

    @NotNull
    public StepikSubmissionsGetQuery order(@NotNull Order value) {
        addParam("order", value.toString());
        return this;
    }

    @NotNull
    public StepikSubmissionsGetQuery reviewStatus(@NotNull ReviewStatus value) {
        addParam("review_status", value.toString());
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.SUBMISSIONS;
    }

    @NotNull
    public StepikSubmissionsGetQuery page(int value) {
        addParam("page", value);
        return this;
    }
}
