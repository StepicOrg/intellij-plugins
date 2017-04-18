package org.stepik.api.objects.reviews;

import org.stepik.api.objects.AbstractObject;

/**
 * @author meanmail
 */
public class RubricScore extends AbstractObject {
    private String review;
    private String rubric;
    private int score;
    private String text;

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRubric() {
        return rubric;
    }

    public void setRubric(String rubric) {
        this.rubric = rubric;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
