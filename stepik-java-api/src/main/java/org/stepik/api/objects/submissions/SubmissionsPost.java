package org.stepik.api.objects.submissions;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class SubmissionsPost {
    private SubmissionPost submission;

    @NotNull
    public SubmissionPost getSubmission() {
        if (submission == null) {
            submission = new SubmissionPost();
        }
        return submission;
    }
}
