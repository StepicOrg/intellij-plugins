package org.stepic.plugin.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.jetbrains.edu.learning.StudyState;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.edu.learning.StudyUtils;
import com.jetbrains.edu.learning.actions.StudyCheckAction;
import com.jetbrains.edu.learning.checker.StudyCheckUtils;
import com.jetbrains.edu.learning.courseFormat.StudyStatus;
import com.jetbrains.edu.learning.courseFormat.Task;
import com.jetbrains.edu.learning.editor.StudyEditor;
import com.jetbrains.edu.learning.stepic.StepicConnectorGet;
import com.jetbrains.edu.learning.stepic.StepicConnectorPost;
import com.jetbrains.edu.learning.stepic.StepicWrappers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StepicJavaPostAction extends StudyCheckAction {
    private static final Logger LOG = Logger.getInstance(StepicJavaPostAction.class);

    @Override
    public void check(@NotNull Project project) {
        LOG.warn("check is started");
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
                LOG.warn(task.getName());
                LOG.warn(Integer.toString(task.getStepicId()));

                int intAttemptId = StepicConnectorPost.getAttempt(task.getStepicId()).attempts.get(0).id;
                String attemptId = Integer.toString(intAttemptId);
                LOG.warn("att id = " + attemptId);
//                studyState.getVirtualFile().get
                Document document = FileDocumentManager.getInstance().getDocument(studyState.getVirtualFile());
//                int attempt = StepicConnectorPost.postSubmission(task.getFile("Main.java").text, attemptId).submission.attempt;
//                StepicWrappers.SubmissionContainer container = StepicConnectorPost.postSubmission(document.getText(), attemptId);
                String currentLang = StudyTaskManager.getInstance(project).getLang(task);
                StepicWrappers.SubmissionToPostWrapper sTPW = new StepicWrappers.SubmissionToPostWrapper(attemptId, currentLang, document.getText());
                StepicWrappers.SubmissionContainer container = StepicConnectorPost.postSubmission(sTPW);
                List<StepicWrappers.SubmissionContainer.Submission> submissions = container.submissions;
                int submissionId = submissions.get(0).id;
                LOG.warn("submissionId = " + submissionId);

                final Application application = ApplicationManager.getApplication();
                final int finalSubmissionId = submissionId;
                application.executeOnPooledThread(
                        () -> {
                            String ans = "evaluation";
                            final int TIMER = 2;
                            int count = 0;
                            Notification notification = null;
                            String b = "";
                            while (ans.equals("evaluation") && count < 100) {
                                try {
                                    Thread.sleep(TIMER * 1000);          //1000 milliseconds is one second.
                                    StepicWrappers.ResultSubmissionWrapper submissionWrapper = StepicConnectorGet.getStatus(finalSubmissionId);
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
        return "StepicJavaPostAction";
    }
}
