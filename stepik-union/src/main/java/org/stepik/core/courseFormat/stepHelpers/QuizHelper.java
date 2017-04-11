package org.stepik.core.courseFormat.stepHelpers;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.attempts.Attempt;
import org.stepik.api.objects.attempts.Attempts;
import org.stepik.api.objects.attempts.Component;
import org.stepik.api.objects.attempts.Dataset;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.submissions.Reply;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.objects.users.User;
import org.stepik.api.queries.Order;
import org.stepik.api.queries.submissions.StepikSubmissionsGetQuery;
import org.stepik.api.urls.Urls;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyStatus;
import org.stepik.core.stepik.StepikConnectorLogin;

import java.util.List;

/**
 * @author meanmail
 */
public class QuizHelper extends StepHelper {
    private static final Logger logger = Logger.getInstance(QuizHelper.class);
    private static final String ACTIVE = "active";
    private static final String ACTIVE_WRONG = "active_wrong";
    @NotNull
    Reply reply = new Reply();
    boolean useLastSubmission;
    @NotNull
    private String status = "";
    @NotNull
    private Attempt attempt = new Attempt();
    private int submissionsCount = -1;
    private Submission submission;
    private boolean initialized;

    public QuizHelper(@NotNull Project project, @NotNull StepNode stepNode) {
        super(project, stepNode);
    }

    private boolean loadAttempt(@NotNull StepikApiClient stepikApiClient, long userId) {
        Attempts attempts = stepikApiClient.attempts()
                .get()
                .step(getStepNode().getId())
                .user(userId)
                .execute();
        if (attempts.isEmpty()) {
            attempt = new Attempt();
            return false;
        }

        attempt = attempts.getAttempts().get(0);
        return true;
    }

    private boolean loadSubmission(StepikApiClient stepikApiClient, long userId) {
        long attemptId = attempt.getId();
        StepikSubmissionsGetQuery query = stepikApiClient.submissions()
                .get()
                .order(Order.DESC)
                .user(userId);

        if (!useLastSubmission) {
            query.attempt(attemptId);
        }

        Submissions submissions = query.execute();

        if (!submissions.isEmpty()) {
            submission = submissions.getSubmissions().get(0);
            reply = submission.getReply();
            status = submission.getStatus();
            if (ACTIVE.equals(attempt.getStatus())) {
                if (submission.getAttempt() != attemptId) {
                    status = ACTIVE;
                } else if (status.equals("wrong")) {
                    status = ACTIVE_WRONG;
                }
            }
            getStepNode().setStatus(StudyStatus.of(status));
            return true;
        }

        status = ACTIVE;
        return false;
    }

    @NotNull
    public String getStatus() {
        initStepOptions();
        return status;
    }

    @NotNull
    public String getPath() {
        return getStepNode().getPath();
    }

    public long getAttemptId() {
        initStepOptions();
        return attempt.getId();
    }

    @NotNull
    Dataset getDataset() {
        initStepOptions();
        return attempt.getDataset();
    }

    @NotNull
    public String getDatasetUrl() {
        initStepOptions();
        return attempt.getDatasetUrl();
    }

    public int getTimeLeft() {
        initStepOptions();
        return attempt.getTimeLeft();
    }

    @NotNull
    public String getReplyUrl() {
        initStepOptions();
        return submission != null ? submission.getReplyUrl() : "";
    }

    public String getBaseUrl() {
        return Urls.STEPIK_URL;
    }

    void initStepOptions() {
        if (!needInit()) {
            return;
        }

        onStartInit();
        status = "";

        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
            User user = StepikConnectorLogin.getCurrentUser();
            long userId = user.getId();

            if (!loadAttempt(stepikApiClient, userId)) {
                return;
            }

            onAttemptLoaded();

            if (loadSubmission(stepikApiClient, userId)) {
                onSubmissionLoaded();
            }

            onFinishInit();
        } catch (StepikClientException e) {
            logger.warn("Failed init test-step options", e);
            onInitFailed();
        }
    }

    boolean needInit() {
        return !initialized;
    }

    void onStartInit() {
    }

    void onAttemptLoaded() {
    }

    void onSubmissionLoaded() {
    }

    void onFinishInit() {
        initialized = true;
    }

    void onInitFailed() {
        initialized = false;
    }

    public int getSubmissionsCount() {
        if (submissionsCount == -1) {
            try {
                StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
                User user = StepikConnectorLogin.getCurrentUser();
                long userId = user.getId();
                submissionsCount = 0;
                Submissions submissions;
                int page = 1;
                do {
                    submissions = stepikApiClient.submissions()
                            .get()
                            .step(getStepNode().getId())
                            .user(userId)
                            .page(page)
                            .execute();
                    submissionsCount += submissions.getCount();
                    page++;
                } while (submissions.getMeta().getHasNext());
            } catch (StepikClientException e) {
                logger.warn("Failed get submissions count", e);
                return 0;
            }
        }
        return submissionsCount;
    }

    @NotNull
    public List<Component> getComponents() {
        return getDataset().getComponents();
    }

    public List<String> getBlanks() {
        initStepOptions();
        return reply.getBlanks();
    }

    @NotNull
    public String getFormula() {
        initStepOptions();
        return reply.getFormula();
    }

    @NotNull
    public String getHint() {
        initStepOptions();
        return submission.getHint();
    }

    @NotNull
    public String getLinkTitle() {
        return "View this step on Stepik";
    }

    public boolean isHasSubmissionsRestrictions() {
        Step data = getStepNode().getData();
        return data != null && data.isHasSubmissionsRestrictions();
    }

    public int getMaxSubmissionsCount() {
        Step data = getStepNode().getData();
        if (data == null) {
            return 0;
        }
        return data.getMaxSubmissionsCount();
    }
}
