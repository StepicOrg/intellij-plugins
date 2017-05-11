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

import java.util.List;

import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.getCurrentUser;

/**
 * @author meanmail
 */
public class QuizHelper extends StepHelper {
    private static final Logger logger = Logger.getInstance(QuizHelper.class);
    private static final String ACTIVE = "active";
    private static final String GET_ATTEMPT = "get_attempt";
    private static final String GET_FIRST_ATTEMPT = "get_first_attempt";
    private static final String SUBMIT = "submit";
    private static final String UNCHECKED = "unchecked";
    @NotNull
    Reply reply = new Reply();
    boolean useLastSubmission;
    @NotNull
    private String action = "get_first_attempt";
    @NotNull
    private String status = "unchecked";
    @NotNull
    private Attempt attempt = new Attempt();
    private int submissionsCount = -1;
    private Submission submission;
    private boolean initialized;
    private boolean modified;

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
            action = GET_FIRST_ATTEMPT;
            return false;
        }

        attempt = attempts.getFirst();
        action = ACTIVE.equals(attempt.getStatus()) ? SUBMIT : GET_ATTEMPT;
        return true;
    }

    private void loadSubmission(StepikApiClient stepikApiClient, long userId) {
        long attemptId = attempt.getId();
        StepNode stepNode = getStepNode();

        StepikSubmissionsGetQuery query = stepikApiClient.submissions()
                .get()
                .order(Order.DESC)
                .user(userId)
                .step(stepNode.getId());

        if (!useLastSubmission) {
            query.attempt(attemptId);
        }

        Submissions submissions = query.execute();
        modified = false;

        if (!submissions.isEmpty()) {
            submission = submissions.getFirst();

            boolean lastSubmission = submission.getId() == stepNode.getLastSubmissionId();
            boolean outdated = stepNode.getLastReplyTime().after(submission.getTime());
            if (lastSubmission && outdated) {
                reply = stepNode.getLastReply();
                modified = !submission.getReply().equals(reply);
            } else {
                reply = submission.getReply();
                stepNode.setLastReply(submission.getReply());
                stepNode.setLastSubmissionId(submission.getId());
            }
            if (attemptId == submission.getAttempt()) {
                status = submission.getStatus();
            }
            if (ACTIVE.equals(attempt.getStatus()) && status.equals("correct")) {
                action = GET_ATTEMPT;
            }

            stepNode.setStatus(StudyStatus.of(status));
        } else {
            reply = stepNode.getLastReply();
            modified = true;
        }
    }

    @Override
    @NotNull
    public String getStatus() {
        initStepOptions();
        return status;
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

    @SuppressWarnings("SameReturnValue")
    public String getBaseUrl() {
        return Urls.STEPIK_URL;
    }

    void initStepOptions() {
        if (initialized) {
            return;
        }

        initialized = true;
        status = UNCHECKED;
        action = GET_FIRST_ATTEMPT;

        try {
            StepikApiClient stepikApiClient = authAndGetStepikApiClient();
            User user = getCurrentUser();
            if (user.isGuest()) {
                action = NEED_LOGIN;
                fail();
                initialized = false;
                return;
            }

            long userId = user.getId();

            if (!loadAttempt(stepikApiClient, userId)) {
                fail();
                initialized = false;
                return;
            }

            loadSubmission(stepikApiClient, userId);

            done();
            initialized = true;
        } catch (StepikClientException e) {
            logger.warn("Failed init test-step options", e);
            fail();
        }
    }

    void done() {
    }

    void fail() {
    }

    public int getSubmissionsCount() {
        if (submissionsCount == -1) {
            try {
                StepikApiClient stepikApiClient = authAndGetStepikApiClient();
                User user = getCurrentUser();
                if (user.isGuest()) {
                    action = NEED_LOGIN;
                    return 0;
                }
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

    @Override
    @NotNull
    public String getAction() {
        initStepOptions();
        return action;
    }

    public boolean isModified() {
        return modified;
    }
}
