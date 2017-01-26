package org.stepik.plugin.actions.step;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.attempts.Attempts;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.plugin.utils.DirectivesUtils;

import java.util.List;

public class StepikJavaPostAction extends StudyCheckAction {
    private static final String EVALUATION = "evaluation";
    private static final Logger logger = Logger.getInstance(StepikJavaPostAction.class);
    private static final String ACTION_ID = "STEPIC.StepikJavaPostAction";
    private static final int PERIOD = 2 * 1000; //ms
    private static final int FIVE_MINUTES = 5 * ActionUtils.MILLISECONDS_IN_MINUTES; //ms


    public StepikJavaPostAction() {
    }

    @Nullable
    private static Long sendStep(@NotNull Project project, @NotNull StepNode stepNode) {
        long stepId = stepNode.getId();

        logger.info(String.format("Start sending step: id=%s", stepId));

        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();

        Long intAttemptId = getAttemptId(project, stepikApiClient, stepNode);
        if (intAttemptId == null) {
            return null;
        }

        Long submissionId = getSubmissionId(project, stepikApiClient, stepNode, intAttemptId);
        if (submissionId == null) {
            return null;
        }

        logger.info(String.format("Finish sending step: id=%s", stepId));

        if (isCanceled()) {
            return null;
        }

        return submissionId;
    }

    @Nullable
    private static Long getAttemptId(
            @NotNull Project project,
            @NotNull StepikApiClient stepikApiClient,
            @NotNull StepNode stepNode) {
        Attempts attempts;
        try {
            attempts = stepikApiClient.attempts()
                    .post()
                    .step(stepNode.getId())
                    .execute();
            if (attempts.isEmpty()) {
                notifyFailed(project, stepNode, "Attempts is Empty", null);
                return null;
            }
        } catch (StepikClientException e) {
            notifyFailed(project, stepNode, "Failed post attempt", e);
            return null;
        }

        return attempts.getAttempts().get(0).getId();
    }

    @Nullable
    private static Long getSubmissionId(
            @NotNull Project project,
            @NotNull StepikApiClient stepikApiClient,
            @NotNull StepNode stepNode,
            long intAttemptId) {
        if (!ActionUtils.checkLangSettings(stepNode, project)) {
            return null;
        }

        SupportedLanguages currentLang = stepNode.getCurrentLang();

        String code = getCode(project, stepNode, currentLang);
        if (code == null) {
            logger.info(String.format("Sending step failed: id=%s. Step content is null", stepNode.getId()));
            return null;
        }

        Submissions submissions;
        try {
            submissions = stepikApiClient.submissions()
                    .post()
                    .attempt(intAttemptId)
                    .language(currentLang.getName())
                    .code(code)
                    .execute();
            if (submissions.isEmpty()) {
                notifyFailed(project, stepNode, "Submissions is empty", null);
                return null;
            }
        } catch (StepikClientException e) {
            notifyFailed(project, stepNode, "Failed post submission", e);
            return null;
        }

        List<Submission> submissionsList = submissions.getSubmissions();

        return submissionsList.get(0).getId();
    }

    @Nullable
    private static String getCode(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull SupportedLanguages currentLang) {
        String activateFileName = currentLang.getMainFileName();
        String mainFilePath = String.join("/", stepNode.getPath(), EduNames.SRC, activateFileName);
        VirtualFile mainFile = project.getBaseDir().findFileByRelativePath(mainFilePath);
        if (mainFile == null) {
            return null;
        }

        String[] text = DirectivesUtils.getFileText(mainFile);
        return DirectivesUtils.getTextUnderDirectives(text, currentLang);
    }

    private static void notifyFailed(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull String message,
            @Nullable StepikClientException exception) {
        logger.warn(message, exception);
        String title = String.format("Failed send %s", stepNode.getName());
        String content = "Did't send";
        ActionUtils.notifyError(project, title, content);
    }

    private static boolean isCanceled() {
        try {
            ProgressManager.checkCanceled();
        } catch (ProcessCanceledException e) {
            return true;
        }
        return false;
    }

    private static void sendMetric(@NotNull StepNode stepNode) {
        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
        CourseNode courseNode = stepNode.getCourse();

        try {
            stepikApiClient.metrics()
                    .post()
                    .name("ide_plugin")
                    .tags("name", "S_Union")
                    .tags("action", "send")
                    .data("courseId", courseNode != null ? courseNode.getId() : 0)
                    .data("stepId", stepNode.getId())
                    .execute();
        } catch (StepikClientException e) {
            logger.warn("Failed send metric", e);
            return;
        }
        logger.info("Sending metric was successfully");
    }

    private static void checkStepStatus(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            final long submissionId,
            @NotNull ProgressIndicator indicator) {
        String stepIdString = "id=" + stepNode.getId();
        logger.info("Started check a status for step: " + stepIdString);
        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
        String stepStatus = EVALUATION;
        int timer = 0;
        String hint;
        indicator.setIndeterminate(false);

        Submission currentSubmission = null;
        while (EVALUATION.equals(stepStatus) && timer < FIVE_MINUTES) {
            try {
                Thread.sleep(PERIOD);
                if (isCanceled()) {
                    return;
                }
                timer += PERIOD;
                Submissions submission = stepikApiClient.submissions()
                        .get()
                        .id(submissionId)
                        .execute();

                if (!submission.isEmpty()) {
                    currentSubmission = submission.getSubmissions().get(0);
                    ActionUtils.setupCheckProgress(indicator, currentSubmission, timer);
                    stepStatus = currentSubmission.getStatus();
                }
            } catch (StepikClientException | InterruptedException e) {
                ActionUtils.notifyError(project, "Error", "Get Status error");
                logger.info("Stop check a status for step: " + stepIdString, e);
                return;
            }
        }
        if (currentSubmission == null) {
            logger.info(String.format("Stop check a status for step: %s without result", stepIdString));
            return;
        }

        indicator.setIndeterminate(true);
        indicator.setText("");
        hint = currentSubmission.getHint();
        notify(project, stepNode, stepStatus, hint);
        ProjectView.getInstance(project).refresh();
        logger.info(String.format("Finish check a status for step: %s with status: %s", stepIdString, stepStatus));
    }

    private static void notify(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @Nullable String stepStatus,
            @NotNull String hint) {
        StudyStatus status = StudyStatus.of(stepStatus);

        NotificationType notificationType;
        if (status == StudyStatus.SOLVED) {
            notificationType = NotificationType.INFORMATION;
            hint = "Success!";
        } else {
            notificationType = NotificationType.WARNING;
        }

        stepNode.setStatus(status);

        String title = String.format("%s is %s", stepNode.getName(), stepStatus);
        ActionUtils.notify(project, title, hint, notificationType);
    }

    @Override
    public void check(@NotNull Project project) {
        logger.info("Start checking step");
        StepNode stepNode = StudyUtils.getSelectedStep(project);
        if (stepNode == null) {
            logger.info("Stop checking step: step is null");
            return;
        }

        String title = "Checking Step: " + stepNode.getName();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                Long submissionId = sendStep(project, stepNode);

                if (submissionId == null) {
                    return;
                }

                sendMetric(stepNode);

                checkStepStatus(project, stepNode, submissionId, indicator);
                logger.info(String.format("Finish checking step: id=%s", stepNode.getId()));
            }
        });
    }

    @NotNull
    @Override
    public String getActionId() {
        return ACTION_ID;
    }
}
