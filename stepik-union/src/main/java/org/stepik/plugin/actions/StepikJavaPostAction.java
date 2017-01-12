package org.stepik.plugin.actions;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.actions.StudyCheckAction;
import com.jetbrains.tmp.learning.checker.StudyCheckUtils;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.attempts.Attempts;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.plugin.utils.DirectivesUtils;

import java.util.List;

import static org.stepik.plugin.actions.ActionUtils.checkLangSettings;

public class StepikJavaPostAction extends StudyCheckAction {
    private static final Logger logger = Logger.getInstance(StepikJavaPostAction.class);
    private static final String ACTION_ID = "STEPIC.StepikJavaPostAction";
    private static final int PERIOD = 2 * 1000; // ms
    private static final int FIVE_MINUTES = 5 * 60 * 1000; //ms

    @NotNull
    @Override
    public String getActionId() {
        return ACTION_ID;
    }

    @Override
    public void check(@NotNull Project project) {
        logger.info("check is started");
        ApplicationManager.getApplication().runWriteAction(() ->
                CommandProcessor.getInstance().runUndoTransparentAction(() -> {
                    if (StudyCheckUtils.hasBackgroundProcesses(project)) {
                        return;
                    }

                    StepNode stepNode = StudyUtils.getSelectedStep(project);
                    if (stepNode == null) {
                        return;
                    }

                    if (!checkLangSettings(stepNode, project)) {
                        return;
                    }
                    StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();
                    long intAttemptId;
                    StepikClientException exception = null;
                    Attempts attempts = null;
                    try {
                        attempts = stepikApiClient.attempts()
                                .post()
                                .step(stepNode.getId())
                                .execute();
                    } catch (StepikClientException e) {
                        exception = e;
                    }

                    if (exception != null || attempts.isEmpty()) {
                        logger.warn("Failed post attempt ", exception);
                        Notification notification = new Notification(
                                "Step.sending",
                                stepNode.getName() + " Failed send",
                                "Did't send",
                                NotificationType.ERROR);
                        notification.notify(project);
                        return;
                    }
                    intAttemptId = attempts.getAttempts().get(0).getId();

                    SupportedLanguages currentLang = stepNode.getCurrentLang();
                    String activateFileName = currentLang.getMainFileName();

                    String mainFilePath = String.join("/", stepNode.getPath(), EduNames.SRC, activateFileName);
                    VirtualFile mainFile = project.getBaseDir().findFileByRelativePath(mainFilePath);
                    if (mainFile == null) {
                        return;
                    }

                    String[] text = DirectivesUtils.getFileText(mainFile);
                    String code = DirectivesUtils.getTextUnderDirectives(text, currentLang);

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
                        Notification notification = new Notification(
                                "Step.sending",
                                stepNode.getName() + " Failed send",
                                "Did't send",
                                NotificationType.ERROR);
                        notification.notify(project);
                        return;
                    }
                    List<Submission> submissionsList = submissions.getSubmissions();
                    CourseNode courseNode = stepNode.getCourse();
                    stepikApiClient.metrics()
                            .post()
                            .name("ide_plugin")
                            .tags("name", "S_Union")
                            .tags("action", "send")
                            .data("courseId", courseNode != null ? courseNode.getId() : 0)
                            .data("stepId", stepNode.getId())
                            .execute();

                    long submissionId = submissionsList.get(0).getId();
                    logger.info("submissionId = " + submissionId);

                    final Application application = ApplicationManager.getApplication();
                    final long finalSubmissionId = submissionId;
                    application.executeOnPooledThread(
                            () -> {
                                String stepStatus = "evaluation";
                                int timer = 0;
                                String hint = "";
                                while ("evaluation".equals(stepStatus) && timer < FIVE_MINUTES) {
                                    try {
                                        Thread.sleep(PERIOD);
                                        timer += PERIOD;
                                        Submissions submission = stepikApiClient.submissions()
                                                .get()
                                                .id(finalSubmissionId)
                                                .execute();

                                        if (!submission.isEmpty()) {
                                            Submission currentSubmission = submission.getSubmissions().get(0);
                                            stepStatus = currentSubmission.getStatus();
                                            hint = currentSubmission.getHint();
                                        }
                                    } catch (StepikClientException | InterruptedException e) {
                                        Notification notification = new Notification(
                                                "Step.sending",
                                                "Error",
                                                "Get Status error",
                                                NotificationType.ERROR);
                                        notification.notify(project);
                                        return;
                                    }
                                }

                                NotificationType notificationType;
                                if ("correct".equals(stepStatus)) {
                                    notificationType = NotificationType.INFORMATION;
                                    hint = "Success!";
                                    stepNode.setStatus(StudyStatus.SOLVED);
                                } else {
                                    notificationType = NotificationType.WARNING;
                                    if (stepNode.getStatus() != StudyStatus.SOLVED)
                                        stepNode.setStatus(StudyStatus.FAILED);
                                }
                                Notification notification = new Notification(
                                        "Step.sending",
                                        stepNode.getName() + " is " + stepStatus,
                                        hint,
                                        notificationType);
                                notification.notify(project);
                                ProjectView.getInstance(project).refresh();
                            }
                    );
                }));
    }

    @Override
    public void update(AnActionEvent e) {
        StudyUtils.updateAction(e);

        Project project = e.getProject();
        if (project == null) {
            return;
        }

        StepNode targetStepNode = StudyUtils.getSelectedStep(project);
        e.getPresentation().setEnabled(targetStepNode != null);
    }
}