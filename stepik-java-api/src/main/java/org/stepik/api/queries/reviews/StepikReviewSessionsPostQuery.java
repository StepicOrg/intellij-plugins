package org.stepik.api.queries.reviews;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.reviews.ReviewSessions;
import org.stepik.api.objects.reviews.ReviewSessionsPost;
import org.stepik.api.queries.StepikAbstractPostQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikReviewSessionsPostQuery extends StepikAbstractPostQuery<ReviewSessions> {
    private final ReviewSessionsPost reviewSessions = new ReviewSessionsPost();

    public StepikReviewSessionsPostQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, ReviewSessions.class);
    }

    @NotNull
    public StepikReviewSessionsPostQuery submission(long id) {
        reviewSessions.getReviewSession().setSubmission(id);
        return this;
    }

    @NotNull
    @Override
    protected String getBody() {
        return getJsonConverter().toJson(reviewSessions);
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.ATTEMPTS;
    }
}
