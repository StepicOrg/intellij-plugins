package com.jetbrains.tmp.learning.stepik.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class SubmissionContainer extends AbstractContainer {
    @Expose
    private List<Submission> submissions;

    public SubmissionContainer(int attempt, String score, ArrayList<SolutionFile> files) {
        submissions = new ArrayList<>();
        submissions.add(new Submission(score, attempt, files));
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }
}