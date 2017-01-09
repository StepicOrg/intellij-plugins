package org.stepik.api.objects.progresses;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class Progress {
    private String id;
    @SerializedName("last_viewed")
    private int lastViewed;
    private double score;
    private int cost;
    @SerializedName("n_steps")
    private int nSteps;
    @SerializedName("n_steps_passed")
    private int nStepsPassed;
    @SerializedName("is_passed")
    private boolean isPassed;

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    public int getLastViewed() {
        return lastViewed;
    }

    public void setLastViewed(int lastViewed) {
        this.lastViewed = lastViewed;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getNSteps() {
        return nSteps;
    }

    public void setNSteps(int nSteps) {
        this.nSteps = nSteps;
    }

    public int getNStepsPassed() {
        return nStepsPassed;
    }

    public void setNStepsPassed(int nStepsPassed) {
        this.nStepsPassed = nStepsPassed;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }
}
