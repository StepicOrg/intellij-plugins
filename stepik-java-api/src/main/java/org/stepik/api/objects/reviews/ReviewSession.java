package org.stepik.api.objects.reviews;

import com.google.gson.annotations.SerializedName;
import org.stepik.api.objects.AbstractObject;

/**
 * @author meanmail
 */
public class ReviewSession extends AbstractObject {
    private String instruction;
    private String submission;
    @SerializedName("given_reviews")
    private String givenReviews;
    @SerializedName("given_reviews")
    private String isGivingStarted;
    @SerializedName("given_reviews")
    private String isGivingFinished;
    @SerializedName("given_reviews")
    private String takenReviews;
    @SerializedName("given_reviews")
    private String isTakingStarted;
    @SerializedName("given_reviews")
    private String isTakingFinished;
    @SerializedName("given_reviews")
    private String isReviewAvailable;
    @SerializedName("given_reviews")
    private String isFinished;
    private int score;
    @SerializedName("given_reviews")
    private String availableReviewsCount;
    @SerializedName("given_reviews")
    private String activeReview;

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getSubmission() {
        return submission;
    }

    public void setSubmission(String submission) {
        this.submission = submission;
    }

    public String getGivenReviews() {
        return givenReviews;
    }

    public void setGivenReviews(String givenReviews) {
        this.givenReviews = givenReviews;
    }

    public String getIsGivingStarted() {
        return isGivingStarted;
    }

    public void setIsGivingStarted(String isGivingStarted) {
        this.isGivingStarted = isGivingStarted;
    }

    public String getIsGivingFinished() {
        return isGivingFinished;
    }

    public void setIsGivingFinished(String isGivingFinished) {
        this.isGivingFinished = isGivingFinished;
    }

    public String getTakenReviews() {
        return takenReviews;
    }

    public void setTakenReviews(String takenReviews) {
        this.takenReviews = takenReviews;
    }

    public String getIsTakingStarted() {
        return isTakingStarted;
    }

    public void setIsTakingStarted(String isTakingStarted) {
        this.isTakingStarted = isTakingStarted;
    }

    public String getIsTakingFinished() {
        return isTakingFinished;
    }

    public void setIsTakingFinished(String isTakingFinished) {
        this.isTakingFinished = isTakingFinished;
    }

    public String getIsReviewAvailable() {
        return isReviewAvailable;
    }

    public void setIsReviewAvailable(String isReviewAvailable) {
        this.isReviewAvailable = isReviewAvailable;
    }

    public String getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(String isFinished) {
        this.isFinished = isFinished;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAvailableReviewsCount() {
        return availableReviewsCount;
    }

    public void setAvailableReviewsCount(String availableReviewsCount) {
        this.availableReviewsCount = availableReviewsCount;
    }

    public String getActiveReview() {
        return activeReview;
    }

    public void setActiveReview(String activeReview) {
        this.activeReview = activeReview;
    }
}
