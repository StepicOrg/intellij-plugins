package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.attempts.Attempt;
import org.stepik.api.objects.attempts.Attempts;
import org.stepik.api.objects.attempts.Dataset;
import org.stepik.api.objects.submissions.Reply;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.queries.Order;

/**
 * @author meanmail
 */
public class StringStepNodeHelper extends StepHelper {
    private static final Logger logger = Logger.getInstance(ChoiceStepNodeHelper.class);
    private boolean isTextDisabled;
    private String status;
    private long attemptId;
    private String text;

    StringStepNodeHelper(@NotNull StepNode stepNode) {
        super(stepNode);
    }

    @NotNull
    public StepNode getStepNode() {
        return stepNode;
    }

    @NotNull
    public String getText() {
        initStepOptions();
        return text;
    }

    private void initStepOptions() {
        if (text != null) {
            return;
        }
        text = "";
        status = "empty";
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();

            Attempts attempts = stepikApiClient.attempts()
                    .get()
                    .step(stepNode.getId())
                    .execute();
            if (attempts.isEmpty()) {
                return;
            }

            Attempt attempt = attempts.getAttempts().get(0);
            attemptId = attempt.getId();
            Dataset dataset = attempt.getDataset();
            isTextDisabled = dataset.isTextDisabled();

            Submissions submissions = stepikApiClient.submissions()
                    .get()
                    .order(Order.DESC)
                    .attempt(attempt.getId())
                    .execute();

            if (!submissions.isEmpty()) {
                Submission submission = submissions.getSubmissions().get(0);
                Reply reply = submission.getReply();
                text = reply.getText();
                text = text != null ? text : "";
                stepNode.setStatus(StudyStatus.of(submission.getStatus()));
                status = submission.getStatus();
            } else {
                stepNode.setStatus(StudyStatus.UNCHECKED);
                status = "active";
            }
        } catch (StepikClientException e) {
            logger.warn("Failed init text-step options", e);
            text = null;
        }
    }

    public boolean isTextDisabled() {
        initStepOptions();
        return isTextDisabled;
    }

    public String getPath() {
        return stepNode.getPath();
    }

    public String getStatus() {
        initStepOptions();
        return status;
    }

    public long getAttemptId() {
        initStepOptions();
        return attemptId;
    }
}
