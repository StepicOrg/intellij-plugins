package org.stepik.api.objects.reviews;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;
import org.stepik.api.objects.attempts.Attempt;
import org.stepik.api.objects.submissions.Submission;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class ReviewSessions extends ObjectsContainer<ReviewSession> {
    @SerializedName("review-sessions")
    private List<ReviewSession> reviewSessions;
    private List<Attempt> attempts;
    private List<Review> reviews;
    @SerializedName("rubric-scores")
    private List<RubricScore> rubricScores;
    private List<Submission> submissions;

    @NotNull
    public List<Attempt> getAttempts() {
        if (attempts == null) {
            attempts = new ArrayList<>();
        }
        return attempts;
    }

    @NotNull
    public List<Review> getReviews() {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        return reviews;
    }

    public List<RubricScore> getRubricScores() {
        if (rubricScores == null) {
            rubricScores = new ArrayList<>();
        }
        return rubricScores;
    }

    public List<Submission> getSubmissions() {
        if (submissions == null) {
            submissions = new ArrayList<>();
        }
        return submissions;
    }

    @NotNull
    public List<ReviewSession> getReviewSessions() {
        if (reviewSessions == null) {
            reviewSessions = new ArrayList<>();
        }
        return reviewSessions;
    }

    @NotNull
    @Override
    public List<ReviewSession> getItems() {
        return getReviewSessions();
    }

    @NotNull
    public Class<ReviewSession> getItemClass() {
        return ReviewSession.class;
    }
}
