package org.stepik.plugin.actions.step;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.attempts.Attempts;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.utils.Utils;
import org.stepik.plugin.actions.ActionUtils;
import org.stepik.plugin.actions.SendAction;

import static org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED;
import static org.stepik.core.metrics.MetricsStatus.FAILED_POST;
import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.metrics.MetricsStatus.USER_CANCELED;
import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.isAuthenticated;
import static org.stepik.core.utils.DirectivesUtilsKt.getFileText;
import static org.stepik.core.utils.DirectivesUtilsKt.getTextUnderDirectives;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;

public class StepikSendAction extends CodeQuizAction {
    private static final Logger logger = Logger.getInstance(StepikSendAction.class);
    private static final String ACTION_ID = "STEPIC.StepikSendAction";
    private static final String SHORTCUT = "ctrl alt pressed ENTER";
    private static final String SHORTCUT_TEXT = ActionUtils.getShortcutText(SHORTCUT);
    private static final String TEXT = "Check Step (" + SHORTCUT_TEXT + ")";
    private static final String DESCRIPTION = "Check current step";

    public StepikSendAction() {
        super(TEXT, DESCRIPTION, AllStepikIcons.ToolWindow.checkTask);
    }

    @Nullable
    private static Long sendStep(
            @NotNull StepikApiClient stepikApiClient,
            @NotNull Project project,
            @NotNull StepNode stepNode) {
        long stepId = stepNode.getId();

        logger.info(String.format("Start sending step: id=%s", stepId));

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

        if (Utils.INSTANCE.isCanceled()) {
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

        return attempts.getFirst().getId();
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
                    .language(currentLang.getLangName())
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

        return submissions.getFirst().getId();
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

        String text = getFileText(mainFile);
        return getTextUnderDirectives(text, currentLang);
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
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        FileDocumentManager.getInstance().saveAllDocuments();

        ApplicationManager.getApplication()
                .executeOnPooledThread(() -> check(project));
    }

    @Override
    public String[] getShortcuts() {
        return new String[]{SHORTCUT};
    }

    private void check(@NotNull Project project) {
        logger.info("Start checking step");

        StepNode stepNode = getCurrentCodeStepNode(project);
        if (stepNode == null) {
            logger.info("Stop checking step: step is null or it is not StepNode ");
            return;
        }

        String title = "Checking Step: " + stepNode.getName();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                StepikApiClient stepikApiClient = authAndGetStepikApiClient(true);
                if (!isAuthenticated()) {
                    return;
                }

                Long submissionId = sendStep(stepikApiClient, project, stepNode);

                if (submissionId == null) {
                    return;
                }

                Metrics.sendAction(project, stepNode, SUCCESSFUL);

                SendAction.checkStepStatus(project, stepikApiClient, stepNode, submissionId, indicator);
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
