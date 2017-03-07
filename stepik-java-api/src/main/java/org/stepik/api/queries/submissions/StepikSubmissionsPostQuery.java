package org.stepik.api.queries.submissions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.objects.submissions.SubmissionsPost;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

import java.util.List;

/**
 * @author meanmail
 */
public class StepikSubmissionsPostQuery extends StepikAbstractPostQuery<Submissions> {
    private final SubmissionsPost submissions = new SubmissionsPost();

    public StepikSubmissionsPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Submissions.class);
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.SUBMISSIONS;
    }

    @NotNull
    public StepikSubmissionsPostQuery attempt(long id) {
        submissions.getSubmission().setAttempt(id);
        return this;
    }

    @NotNull
    public StepikSubmissionsPostQuery text(@Nullable String text) {
        submissions.getSubmission().getReply().setText(text);
        return this;
    }

    @NotNull
    public StepikSubmissionsPostQuery choices(List<Boolean> choices) {
        submissions.getSubmission().getReply().setChoices(choices);
        return this;
    }

    @NotNull
    public StepikSubmissionsPostQuery language(@NotNull String value) {
        submissions.getSubmission().getReply().setLanguage(value);
        return this;
    }

    @NotNull
    public StepikSubmissionsPostQuery code(@NotNull String value) {
        submissions.getSubmission().getReply().setCode(value);
        return this;
    }

    @NotNull
    @Override
    protected String getBody() {
        return getJsonConverter().toJson(submissions);
    }
}
