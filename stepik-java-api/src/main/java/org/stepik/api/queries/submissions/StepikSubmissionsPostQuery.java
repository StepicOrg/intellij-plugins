package org.stepik.api.queries.submissions;

import com.google.gson.Gson;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.objects.submissions.SubmissionsPost;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikSubmissionsPostQuery extends StepikAbstractPostQuery<Submissions> {
    private final SubmissionsPost submissions = new SubmissionsPost();

    public StepikSubmissionsPostQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Submissions.class);
    }

    @Override
    protected String getUrl() {
        return Urls.SUBMISSIONS;
    }

    public StepikSubmissionsPostQuery attempt(int id) {
        submissions.getSubmission().setAttempt(id);
        return this;
    }

    public StepikSubmissionsPostQuery language(String value) {
        submissions.getSubmission().getReply().setLanguage(value);
        return this;
    }

    public StepikSubmissionsPostQuery code(String value) {
        submissions.getSubmission().getReply().setCode(value);
        return this;
    }

    @Override
    protected String getBody() {
        return new Gson().toJson(submissions);
    }
}
