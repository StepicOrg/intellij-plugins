package org.stepik.plugin.actions;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.metrics.MetricsStatus;
import org.stepik.core.utils.Utils;

import static com.jetbrains.tmp.learning.courseFormat.StudyStatus.SOLVED;
import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.metrics.MetricsStatus.TIME_OVER;
import static org.stepik.core.metrics.MetricsStatus.USER_CANCELED;

/**
 * @author meanmail
 */
public class SendAction {
    private static final Logger logger = Logger.getInstance(SendAction.class);
    private static final String EVALUATION = "evaluation";
    private static final int PERIOD = 2 * 1000; //ms
    private static final int FIVE_MINUTES = 5 * ActionUtils.MILLISECONDS_IN_MINUTES; //ms

    public static void checkStepStatus(
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
        while (timer < FIVE_MINUTES) {
            try {
                Submissions submission = stepikApiClient.submissions()
                        .get()
                        .id(submissionId)
                        .execute();

                if (!submission.isEmpty()) {
                    currentSubmission = submission.getSubmissions().get(0);
                    ActionUtils.setupCheckProgress(indicator, currentSubmission, timer);
                    stepStatus = currentSubmission.getStatus();
                    if (!EVALUATION.equals(stepStatus)) {
                        break;
                    }
                }

                Thread.sleep(PERIOD);
                if (Utils.isCanceled()) {
                    Metrics.getStepStatusAction(project, stepNode, USER_CANCELED);
                    return;
                }
                timer += PERIOD;
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
        MetricsStatus actionStatus = EVALUATION.equals(stepStatus) ? TIME_OVER : SUCCESSFUL;
        Metrics.getStepStatusAction(project, stepNode, actionStatus);

        indicator.setIndeterminate(true);
        indicator.setText("");
        hint = currentSubmission.getHint();
        notify(project, stepNode, stepStatus, hint);
        ApplicationManager.getApplication().invokeLater(() -> {
            ProjectView.getInstance(project).refresh();
            StepikProjectManager.updateSelection(project);
        });
        logger.info(String.format("Finish check a status for step: %s with status: %s", stepIdString, stepStatus));
    }

    private static void notify(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @Nullable String stepStatus,
            @NotNull String hint) {
        NotificationType notificationType;
        if (StudyStatus.of(stepStatus) == SOLVED) {
            notificationType = NotificationType.INFORMATION;
            hint = "Success!";
            stepNode.passed();
        } else {
            notificationType = NotificationType.WARNING;
        }

        String title = String.format("%s is %s", stepNode.getName(), stepStatus);
        ActionUtils.notify(project, title, hint, notificationType);
    }

}
