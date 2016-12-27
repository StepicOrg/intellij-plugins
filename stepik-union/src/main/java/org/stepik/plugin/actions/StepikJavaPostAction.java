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
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Step;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import com.jetbrains.tmp.learning.stepik.StepikConnectorPost;
import com.jetbrains.tmp.learning.stepik.StepikWrappers;
import com.jetbrains.tmp.learning.stepik.entities.Submission;
import com.jetbrains.tmp.learning.stepik.entities.SubmissionContainer;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.utils.DirectivesUtils;

import java.io.IOException;
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

                    Step step = StudyUtils.getSelectedStep(project);
                    if (step == null) {
                        return;
                    }

                    if (!checkLangSettings(step, project)) {
                        return;
                    }

                    int intAttemptId;
                    try {
                        intAttemptId = StepikConnectorPost.getAttempt(step.getId()).attempts.get(0).id;
                    } catch (IOException e) {
                        Notification notification = new Notification(
                                "Step.sending",
                                step.getName() + " IOException",
                                "Did't send",
                                NotificationType.ERROR);
                        notification.notify(project);
                        return;
                    }
                    String attemptId = Integer.toString(intAttemptId);

                    SupportedLanguages currentLang = step.getCurrentLang();
                    String activateFileName = currentLang.getMainFileName();

                    String mainFilePath = String.join("/", step.getPath(), EduNames.SRC, activateFileName);
                    VirtualFile mainFile = project.getBaseDir().findFileByRelativePath(mainFilePath);
                    if (mainFile == null) {
                        return;
                    }

                    String[] text = DirectivesUtils.getFileText(mainFile);
                    String solution = DirectivesUtils.getTextUnderDirectives(text, currentLang);
                    StepikWrappers.SubmissionToPostWrapper postWrapper =
                            new StepikWrappers.SubmissionToPostWrapper(attemptId, currentLang.getName(), solution);
                    SubmissionContainer container = StepikConnectorPost.postSubmission(postWrapper);
                    if (container == null) {
                        return;
                    }
                    List<Submission> submissions = container.getSubmissions();
                    Course course = step.getCourse();
                    StepikWrappers.MetricsWrapper metric = new StepikWrappers.MetricsWrapper(
                            StepikWrappers.MetricsWrapper.PluginNames.STEPIK_UNION,
                            StepikWrappers.MetricsWrapper.MetricActions.POST,
                            course == null ? 0 : course.getId(),
                            step.getId());
                    StepikConnectorPost.postMetric(metric);
                    int submissionId = submissions.get(0).getId();
                    logger.info("submissionId = " + submissionId);

                    final Application application = ApplicationManager.getApplication();
                    final int finalSubmissionId = submissionId;
                    application.executeOnPooledThread(
                            () -> {
                                String stepStatus = "evaluation";
                                int timer = 0;
                                String hint = "";
                                while ("evaluation".equals(stepStatus) && timer < FIVE_MINUTES) {
                                    try {
                                        Thread.sleep(PERIOD);          //1000 milliseconds is one second.
                                        timer += PERIOD;
                                        StepikWrappers.ResultSubmissionWrapper submissionWrapper =
                                                StepikConnectorGet.getStatus(finalSubmissionId);
                                        if (submissionWrapper != null) {
                                            stepStatus = submissionWrapper.submissions[0].status;
                                            hint = submissionWrapper.submissions[0].hint;
                                        }
                                    } catch (InterruptedException e) {
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
                                    step.setStatus(StudyStatus.SOLVED);
                                } else {
                                    notificationType = NotificationType.WARNING;
                                    if (step.getStatus() != StudyStatus.SOLVED)
                                        step.setStatus(StudyStatus.FAILED);
                                }
                                Notification notification = new Notification(
                                        "Step.sending",
                                        step.getName() + " is " + stepStatus,
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

        Step targetStep = StudyUtils.getSelectedStep(project);
        e.getPresentation().setEnabled(targetStep != null);
    }
}