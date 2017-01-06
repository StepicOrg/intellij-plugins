package org.stepik.api.objects.submissions;

/**
 * @author meanmail
 */
public class SubmissionsPost {
    private SubmissionPost submission;

    public SubmissionPost getSubmission() {
        if (submission == null) {
            submission = new SubmissionPost();
        }
        return submission;
    }
}
