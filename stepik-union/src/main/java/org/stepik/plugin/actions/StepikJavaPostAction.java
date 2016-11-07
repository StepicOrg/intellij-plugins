package org.stepik.plugin.actions;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.jetbrains.tmp.learning.LangManager;
import com.jetbrains.tmp.learning.LangSetting;
import com.jetbrains.tmp.learning.StudyState;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyCheckAction;
import com.jetbrains.tmp.learning.checker.StudyCheckUtils;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import com.jetbrains.tmp.learning.stepik.metric.MetricActions;
import com.jetbrains.tmp.learning.stepik.metric.MetricBuilder;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import com.jetbrains.tmp.learning.stepik.StepikConnectorPost;
import com.jetbrains.tmp.learning.stepik.StepikWrappers;
import com.jetbrains.tmp.learning.stepik.metric.MetricsWrapper;
import com.jetbrains.tmp.learning.stepik.metric.PluginNames;
import org.jetbrains.annotations.NotNull;
import com.jetbrains.tmp.learning.stepik.SupportedLanguages;
import org.stepik.plugin.utils.DirectivesUtils;
import org.stepik.plugin.utils.NotificationUtils;

import java.io.IOException;
import java.util.List;

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
                    final StudyEditor selectedEditor = StudyUtils.getSelectedStudyEditor(project);
                    if (selectedEditor == null) return;
                    final StudyState studyState = new StudyState(selectedEditor);
                    if (!studyState.isValid()) {
                        logger.info("StudyCheckAction was invoked outside study editor");
                        return;
                    }
                    if (StudyCheckUtils.hasBackgroundProcesses(project)) return;

                    ApplicationManager.getApplication().invokeLater(
                            () -> IdeFocusManager
                                    .getInstance(project)
                                    .requestFocus(studyState.getEditor().getComponent(), true));

                    Task task = studyState.getTask();

                    int intAttemptId = 0;
                    try {
                        intAttemptId = StepikConnectorPost.getAttempt(task.getStepId()).attempts.get(0).id;
                    } catch (IOException e) {
                        Notification notification = new Notification(
                                "Step.sending",
                                task.getName() + " IOException",
                                "Did't send",
                                NotificationType.ERROR);
                        NotificationUtils.showNotification(notification, project);
                        return;
                    }
                    String attemptId = Integer.toString(intAttemptId);

                    LangManager langManager = StudyTaskManager.getInstance(project).getLangManager();
                    LangSetting taskLangSetting = langManager.getLangSetting(task);
                    SupportedLanguages currentLang = SupportedLanguages.langOf(taskLangSetting.getCurrentLang());
                    if (currentLang == null) {
                        return;
                    }
                    String[] text = DirectivesUtils.getFileText(studyState.getVirtualFile());
                    String solution = DirectivesUtils.getTextUnderDirectives(text, currentLang);
                    StepikWrappers.SubmissionToPostWrapper postWrapper =
                            new StepikWrappers.SubmissionToPostWrapper(attemptId, currentLang.getName(), solution);
                    StepikWrappers.SubmissionContainer container = StepikConnectorPost.postSubmission(postWrapper);
                    if (container == null) {
                        return;
                    }
                    List<StepikWrappers.SubmissionContainer.Submission> submissions = container.submissions;
                    MetricsWrapper metric = MetricBuilder.getInstance()
                            .addTag(PluginNames.STEPIK_UNION)
                            .addTag(MetricActions.POST)
                            .addTag(currentLang)
                            .setCourseId(task.getLesson().getSection().getCourse().getId())
                            .setStepId(task.getStepId())
                            .build();
                    StepikConnectorPost.postMetric(metric);
                    int submissionId = submissions.get(0).id;
                    logger.info("submissionId = " + submissionId);

                    final Application application = ApplicationManager.getApplication();
                    final int finalSubmissionId = submissionId;
                    application.executeOnPooledThread(
                            () -> {
                                String taskStatus = "evaluation";
                                int timer = 0;
                                String hint = "";
                                while ("evaluation".equals(taskStatus) && timer < FIVE_MINUTES) {
                                    try {
                                        Thread.sleep(PERIOD);          //1000 milliseconds is one second.
                                        timer += PERIOD;
                                        StepikWrappers.ResultSubmissionWrapper submissionWrapper =
                                                StepikConnectorGet.getStatus(finalSubmissionId);
                                        if (submissionWrapper != null) {
                                            taskStatus = submissionWrapper.submissions[0].status;
                                            hint = submissionWrapper.submissions[0].hint;
                                        }
                                    } catch (InterruptedException e) {
                                        Notification notification = new Notification(
                                                "Step.sending",
                                                "Error",
                                                "Get Status error",
                                                NotificationType.ERROR);
                                        NotificationUtils.showNotification(notification, project);
                                        return;
                                    }
                                }

                                NotificationType notificationType;
                                if ("correct".equals(taskStatus)) {
                                    notificationType = NotificationType.INFORMATION;
                                    hint = "Success!";
                                    task.setStatus(StudyStatus.Solved);
                                } else {
                                    notificationType = NotificationType.WARNING;
                                    if (task.getStatus() != StudyStatus.Solved)
                                        task.setStatus(StudyStatus.Failed);
                                }
                                Notification notification = new Notification(
                                        "Step.sending",
                                        task.getName() + " is " + taskStatus,
                                        hint,
                                        notificationType);
                                NotificationUtils.showNotification(notification, project);
                                ProjectView.getInstance(project).refresh();
                            }
                    );
                }));
    }
}