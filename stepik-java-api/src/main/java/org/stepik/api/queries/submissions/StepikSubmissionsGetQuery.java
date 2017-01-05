package org.stepik.api.queries.submissions;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.queries.Order;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikSubmissionsGetQuery extends StepikAbstractGetQuery<Submissions> {
    public StepikSubmissionsGetQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Submissions.class);
    }

    public StepikSubmissionsGetQuery id(Integer... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikSubmissionsGetQuery status(String value) {
        addParam("status", value);
        return this;
    }

    public StepikSubmissionsGetQuery userName(String value) {
        addParam("user_name", value);
        return this;
    }

    public StepikSubmissionsGetQuery step(int value) {
        addParam("step", value);
        return this;
    }

    public StepikSubmissionsGetQuery user(int value) {
        addParam("user", value);
        return this;
    }

    public StepikSubmissionsGetQuery attempt(int value) {
        addParam("attempt", value);
        return this;
    }

    public StepikSubmissionsGetQuery search(String value) {
        addParam("search", value);
        return this;
    }

    public StepikSubmissionsGetQuery order(Order value) {
        addParam("order", value.toString());
        return this;
    }

    public StepikSubmissionsGetQuery reviewStatus(ReviewStatus value) {
        addParam("review_status", value.toString());
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.SUBMISSIONS;
    }

    public StepikSubmissionsGetQuery page(int value) {
        addParam("page", value);
        return this;
    }
}
