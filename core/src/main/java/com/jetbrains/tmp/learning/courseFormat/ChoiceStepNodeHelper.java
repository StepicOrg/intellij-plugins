package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class ChoiceStepNodeHelper {
    private static final Logger logger = Logger.getInstance(ChoiceStepNodeHelper.class);
    private final StepNode stepNode;
    private boolean isMultipleChoice;
    private List<Pair<String, Boolean>> stepOptions;
    private String status;
    private long attemptId;

    ChoiceStepNodeHelper(@NotNull StepNode stepNode) {
        this.stepNode = stepNode;
    }

    @NotNull
    public StepNode getStepNode() {
        return stepNode;
    }

    @NotNull
    public List<Pair<String, Boolean>> getOptions() {
        initStepOptions();
        return stepOptions;
    }

    private void initStepOptions() {
        if (stepOptions != null) {
            return;
        }
        stepOptions = new ArrayList<>();
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
            isMultipleChoice = dataset.isMultipleChoice();
            String[] options = dataset.getOptions();

            Submissions submissions = stepikApiClient.submissions()
                    .get()
                    .order(Order.DESC)
                    .attempt(attempt.getId())
                    .execute();

            List<Boolean> choices = null;
            if (!submissions.isEmpty()) {
                Submission submission = submissions.getSubmissions().get(0);
                Reply reply = submission.getReply();
                choices = reply.getChoices();
                stepNode.setStatus(StudyStatus.of(submission.getStatus()));
                status = submission.getStatus();
            } else {
                stepNode.setStatus(StudyStatus.UNCHECKED);
                status = "active";
            }

            for (int i = 0; i < options.length; i++) {
                boolean checked = choices != null && choices.get(i);
                stepOptions.add(Pair.create(options[i], checked));
            }
        } catch (StepikClientException e) {
            logger.warn("Failed init test-step options", e);
        }
    }

    public boolean isMultipleChoice() {
        initStepOptions();
        return isMultipleChoice;
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
