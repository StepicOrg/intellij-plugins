package org.stepik.plugin.actions.step;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.attempts.Attempts;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.utils.Utils;
import org.stepik.plugin.actions.ActionUtils;
import org.stepik.plugin.actions.SendAction;
import org.stepik.plugin.utils.DirectivesUtils;

import java.util.List;

import static org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED;
import static org.stepik.core.metrics.MetricsStatus.FAILED_POST;
import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.metrics.MetricsStatus.USER_CANCELED;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;

public class StepikJavaPostAction extends StudyCheckAction {
    private static final Logger logger = Logger.getInstance(StepikJavaPostAction.class);
    private static final String ACTION_ID = "STEPIC.StepikJavaPostAction";

    public StepikJavaPostAction() {
    }

    @Nullable
    private static Long sendStep(@NotNull Project project, @NotNull StepNode stepNode) {
        long stepId = stepNode.getId();

        logger.info(String.format("Start sending step: id=%s", stepId));

        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();

        Long intAttemptId = getAttemptId(project, stepikApiClient, stepNode);
        if (intAttemptId == null) {
            Metrics.sendAction(project, stepNode, DATA_NOT_LOADED);
            return null;
        }

        Long submissionId = getSubmissionId(project, stepikApiClient, stepNode, intAttemptId);
        if (submissionId == null) {
            Metrics.sendAction(project, stepNode, FAILED_POST);
            return null;
        }

        logger.info(String.format("Finish sending step: id=%s", stepId));

        if (Utils.isCanceled()) {
            Metrics.sendAction(project, stepNode, USER_CANCELED);
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
        VirtualFile src = getOrCreateSrcDirectory(project, stepNode, true);
        if (src == null) {
            return null;
        }

        VirtualFile mainFile = src.findChild(currentLang.getMainFileName());
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

    @Override
    public void check(@NotNull Project project) {
        logger.info("Start checking step");
        StudyNode<?, ?> selected = StepikProjectManager.getSelected(project);
        if (!(selected instanceof StepNode)) {
            logger.info("Stop checking step: step is null or is not StepNode ");
            return;
        }

        StepNode stepNode = (StepNode) selected;

        String title = "Checking Step: " + stepNode.getName();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                Long submissionId = sendStep(project, stepNode);

                if (submissionId == null) {
                    return;
                }

                Metrics.sendAction(project, stepNode, SUCCESSFUL);

                SendAction.checkStepStatus(project, stepNode, submissionId, indicator);
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
