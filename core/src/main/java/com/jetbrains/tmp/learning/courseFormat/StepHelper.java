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

import static com.jetbrains.tmp.learning.courseFormat.StudyStatus.UNCHECKED;

/**
 * @author meanmail
 */
public class StepHelper {
    private static final Logger logger = Logger.getInstance(StepHelper.class);
    final StepNode stepNode;
    @NotNull
    Dataset dataset = new Dataset();
    @NotNull
    Reply reply = new Reply();
    @NotNull
    private String status = "empty";
    private long attemptId;

    StepHelper(@NotNull StepNode stepNode) {
        this.stepNode = stepNode;
    }

    @NotNull
    public StepNode getStepNode() {
        return stepNode;
    }

    private boolean loadAttempt(@NotNull StepikApiClient stepikApiClient) {
        Attempts attempts = stepikApiClient.attempts()
                .get()
                .step(stepNode.getId())
                .execute();
        if (attempts.isEmpty()) {
            return false;
        }
        Attempt attempt = attempts.getAttempts().get(0);

        attemptId = attempt.getId();
        dataset = attempt.getDataset();
        return true;
    }


    private boolean loadSubmission(StepikApiClient stepikApiClient) {
        Submissions submissions = stepikApiClient.submissions()
                .get()
                .order(Order.DESC)
                .attempt(attemptId)
                .execute();

        if (!submissions.isEmpty()) {
            Submission submission = submissions.getSubmissions().get(0);
            reply = submission.getReply();
            status = submission.getStatus();
            if (status.isEmpty()) {
                status = "empty";
            }
            stepNode.setStatus(StudyStatus.of(status));
            return true;
        }

        stepNode.setStatus(UNCHECKED);
        status = "active";
        return false;
    }

    @NotNull
    public String getStatus() {
        initStepOptions();
        return status;
    }

    @NotNull
    public String getPath() {
        return stepNode.getPath();
    }

    public long getAttemptId() {
        initStepOptions();
        return attemptId;
    }

    void initStepOptions() {
        if (!needInit()) {
            return;
        }

        onStartInit();
        status = "empty";
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();

            if (!loadAttempt(stepikApiClient)) {
                return;
            }

            onAttemptLoaded();

            if (loadSubmission(stepikApiClient)) {
                onSubmissionLoaded();
            }

            onFinishInit();
        } catch (StepikClientException e) {
            logger.warn("Failed init test-step options", e);
            onInitFailed();
        }
    }

    boolean needInit() {
        return true;
    }

    void onStartInit() {
    }

    void onAttemptLoaded() {
    }

    void onSubmissionLoaded() {
    }

    void onFinishInit() {
    }

    void onInitFailed() {

    }
}
