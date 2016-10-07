package org.stepik.plugin.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.jetbrains.tmp.learning.StudyState;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyCheckAction;
import com.jetbrains.tmp.learning.checker.StudyCheckUtils;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import com.jetbrains.tmp.learning.stepik.StepikConnectorPost;
import com.jetbrains.tmp.learning.stepik.StepikWrappers;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.collective.SupportedLanguages;

import java.util.List;

public class StepikJavaPostAction extends StudyCheckAction {
    private static final Logger LOG = Logger.getInstance(StepikJavaPostAction.class);

    @Override
    public void check(@NotNull Project project) {
        LOG.info("check is started");
        ApplicationManager.getApplication().runWriteAction(() -> {
            CommandProcessor.getInstance().runUndoTransparentAction(() -> {
                final StudyEditor selectedEditor = StudyUtils.getSelectedStudyEditor(project);
                if (selectedEditor == null) return;
                final StudyState studyState = new StudyState(selectedEditor);
                if (!studyState.isValid()) {
                    LOG.info("StudyCheckAction was invoked outside study editor");
                    return;
                }
                if (StudyCheckUtils.hasBackgroundProcesses(project)) return;

                ApplicationManager.getApplication().invokeLater(
                        () -> IdeFocusManager.getInstance(project).requestFocus(studyState.getEditor().getComponent(), true));

                Task task = studyState.getTask();

                int intAttemptId = StepikConnectorPost.getAttempt(task.getStepId()).attempts.get(0).id;
                String attemptId = Integer.toString(intAttemptId);
                String currentLang = StudyTaskManager.getInstance(project).getLangManager().getLangSetting(task).getCurrentLang();
                SupportedLanguages langSetting = SupportedLanguages.loadLangSettings(currentLang);
                String[] text = DirectivesUtils.getFileText(studyState.getVirtualFile());
                String solution = DirectivesUtils.getTextUnderDirectives(text, langSetting);
                StepikWrappers.SubmissionToPostWrapper postWrapper = new StepikWrappers.SubmissionToPostWrapper(attemptId, currentLang, solution);
                StepikWrappers.SubmissionContainer container = StepikConnectorPost.postSubmission(postWrapper);
                List<StepikWrappers.SubmissionContainer.Submission> submissions = container.submissions;
                StepikWrappers.MetricsWrapper metric = new StepikWrappers.MetricsWrapper(
                        StepikWrappers.MetricsWrapper.PluginNames.S_Union,
                        StepikWrappers.MetricsWrapper.MetricActions.POST,
                        task.getLesson().getCourse().getId(),
                        task.getStepId());
                StepikConnectorPost.postMetric(metric);
                int submissionId = submissions.get(0).id;
                LOG.info("submissionId = " + submissionId);

                final Application application = ApplicationManager.getApplication();
                final int finalSubmissionId = submissionId;
                application.executeOnPooledThread(
                        () -> {
                            String ans = "evaluation";
                            final int TIMER = 2;
                            int count = 0;
                            Notification notification = null;
                            String b = "";
                            while (ans.equals("evaluation") && count < 200) {
                                try {
                                    Thread.sleep(TIMER * 1000);          //1000 milliseconds is one second.
                                    StepikWrappers.ResultSubmissionWrapper submissionWrapper = StepikConnectorGet.getStatus(finalSubmissionId);
                                    ans = submissionWrapper.submissions[0].status;
                                    b = submissionWrapper.submissions[0].hint;
                                    count += TIMER;
                                } catch (InterruptedException | NullPointerException e) {
                                    notification = new Notification("Step.sending", "Error", "Get Status error", NotificationType.ERROR);
                                    NotificationUtils.showNotification(notification, project);
                                    return;
                                }
                            }

                            NotificationType notificationType;

                            if (ans.equals("correct")) {
                                notificationType = NotificationType.INFORMATION;
                                b = "Success!";
                                task.setStatus(StudyStatus.Solved);
                            } else {
                                notificationType = NotificationType.WARNING;
                                b = b.split("\\.")[0];
                                if (task.getStatus() != StudyStatus.Solved)
                                    task.setStatus(StudyStatus.Failed);

                            }
                            notification = new Notification("Step.sending", task.getName() + " is " + ans, b, notificationType);
                            NotificationUtils.showNotification(notification, project);
                        }
                );
            });
        });
    }


    @NotNull
    @Override
    public String getActionId() {
        return "STEPIK.StepikJavaPostAction";
    }
}
