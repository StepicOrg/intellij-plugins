package org.stepik.api.queries.submissions;

import com.google.gson.Gson;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.submissions.PostSubmission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikSubmissionsPostQuery extends StepikAbstractPostQuery<Submissions>{
    public StepikSubmissionsPostQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Submissions.class);
    }

    private PostSubmission submission = new PostSubmission();

    @Override
    protected String getUrl() {
        return Urls.SUBMISSIONS;
    }

    public StepikSubmissionsPostQuery attempt(int id) {
        submission.setAttempt(id);
        return this;
    }

    public StepikSubmissionsPostQuery language(String value) {
        submission.getReply().setLanguage(value);
        return this;
    }

    public StepikSubmissionsPostQuery code(String value) {
        submission.getReply().setCode(value);
        return this;
    }

    @Override
    protected String getBody() {
        return "{\"submission\":" + new Gson().toJson(submission) + "}";
    }
}
