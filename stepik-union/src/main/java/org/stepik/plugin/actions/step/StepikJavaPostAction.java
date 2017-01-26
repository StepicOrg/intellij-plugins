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
    private static final Logger logger = Logger.getInstance(StepikJavaPostAction.class);
    private static final String ACTION_ID = "STEPIC.StepikJavaPostAction";
    private static final int PERIOD = 2 * 1000; //ms
    private static final int FIVE_MINUTES = 5 * ActionUtils.MILLISECONDS_IN_MINUTES; //ms


    public StepikJavaPostAction() {
    }

    @Nullable
    private static Long sendStep(@NotNull Project project, StepNode stepNode) {
        long stepId = stepNode.getId();
        String stepIdString = "id=" + stepId;
        logger.info("Sending step: " + stepIdString);

        if (!ActionUtils.checkLangSettings(stepNode, project)) {
            return null;
        }

        SupportedLanguages currentLang = stepNode.getCurrentLang();
        String code = getCode(project, stepNode, currentLang);
        if (code == null) {
            logger.info("Sending step failed: " + stepIdString + ". Step content is null");
            return null;
        }

        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
        StepikClientException exception = null;
        Attempts attempts = null;
        try {
            attempts = stepikApiClient.attempts()
                    .post()
                    .step(stepId)
                    .execute();
        } catch (StepikClientException e) {
            exception = e;
        }

        if (exception != null || attempts.isEmpty()) {
            logger.warn("Failed post attempt ", exception);
            ActionUtils.notifyError(project, stepNode.getName() + " Failed send", "Did't send");
            logger.info("Sending step failed: " + stepIdString, exception);
            return null;
        }

        long intAttemptId = attempts.getAttempts().get(0).getId();

        Submissions submissions = null;
        try {
            submissions = stepikApiClient.submissions()
                    .post()
                    .attempt(intAttemptId)
                    .language(currentLang.getName())
                    .code(code)
                    .execute();
        } catch (StepikClientException e) {
            exception = e;
        }

        if (exception != null || submissions.isEmpty()) {
            logger.warn("Failed post submission ", exception);
            ActionUtils.notifyError(project, stepNode.getName() + " Failed send", "Did't send");
            logger.info("Sending step failed: " + stepIdString, exception);
            return null;
        }
        List<Submission> submissionsList = submissions.getSubmissions();

        Long submissionId = submissionsList.get(0).getId();
        logger.info("End sending step: " + stepIdString);

        if (isCanceled()) {
            return null;
        }

        return submissionId;
    }

    private static boolean isCanceled() {
        try {
            ProgressManager.checkCanceled();
        } catch (ProcessCanceledException e) {
            return true;
        }
        return false;
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

    private static void sendMetric(StepNode stepNode) {
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

    private static void checkStep(
            @NotNull Project project,
            @NotNull ProgressIndicator indicator,
            @NotNull StepNode stepNode,
            final long submissionId) {
        String stepIdString = "id=" + stepNode.getId();
        logger.info("Started check a status for step: " + stepIdString);
        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
        String stepStatus = "evaluation";
        int timer = 0;
        String hint;
        indicator.setIndeterminate(false);

        Submission currentSubmission = null;
        while ("evaluation".equals(stepStatus) && timer < FIVE_MINUTES) {
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
            logger.info("Stop check a status for step: " + stepIdString + " without result");
            return;
        }

        indicator.setIndeterminate(true);
        indicator.setText("");
        hint = currentSubmission.getHint();
        notify(project, stepNode, stepStatus, hint);
        ProjectView.getInstance(project).refresh();
        logger.info("Finish check a status for step: " + stepIdString + " with status: " + stepStatus);
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

        ActionUtils.notify(project, stepNode.getName() + " is " + stepStatus, hint, notificationType);
    }

    @NotNull
    @Override
    public String getActionId() {
        return ACTION_ID;
    }

    @Override
    public void check(@NotNull Project project) {
        logger.info("Start checking step");
        StepNode stepNode = StudyUtils.getSelectedStep(project);
        if (stepNode == null) {
            logger.info("Stop checking step: step is null");
            return;
        }

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Checking Step: " + stepNode.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                Long submissionId = sendStep(project, stepNode);

                String stepIdString = "id=" + stepNode.getId();

                if (submissionId == null) {
                    logger.info("Sending is failed: " + stepIdString);
                    return;
                }

                logger.info("Sending is success: " + stepIdString);

                sendMetric(stepNode);

                checkStep(project, indicator, stepNode, submissionId);
                logger.info("Finish checking step: " + stepIdString);
            }
        });
    }
}
