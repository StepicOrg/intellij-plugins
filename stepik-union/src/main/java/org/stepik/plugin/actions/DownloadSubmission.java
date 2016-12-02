package org.stepik.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import com.jetbrains.tmp.learning.stepik.StepikConnectorPost;
import com.jetbrains.tmp.learning.stepik.StepikWrappers;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.stepik.plugin.actions.ActionUtils.checkLangSettings;

public class DownloadSubmission extends StudyActionWithShortcut {
    private static final String ACTION_ID = "STEPIK.DownloadSubmission";
    private static final String SHORTCUT = "ctrl alt pressed PAGE_DOWN";

    public DownloadSubmission() {
        super("Download submission(" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Download submission", IconLoader.getIcon("/icons/arrow-down.png"));
    }

    @NotNull
    @Override
    public String getActionId() {
        return ACTION_ID;
    }

    @Nullable
    @Override
    public String[] getShortcuts() {
        return new String[]{SHORTCUT};
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        downloadSubmission(e.getProject());
    }

    private void downloadSubmission(@Nullable Project project) {
        if (project == null) {
            return;
        }

        Task targetTask = StudyUtils.getSelectedTask(project);
        if (targetTask == null) {
            return;
        }

        if (!checkLangSettings(targetTask, project)){
            return;
        }

        String stepId = Integer.toString(targetTask.getStepId());
        String userId = Integer.toString(StudyTaskManager.getInstance(project).getUser().getId());

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("step", stepId));
        nvps.add(new BasicNameValuePair("user", userId));
        nvps.add(new BasicNameValuePair("order", "desc"));

        StepikWrappers.SubmissionContainer submissionContainer = StepikConnectorGet.getSubmissions(nvps);
        if (submissionContainer == null) {
            return;
        }
        List<StepikWrappers.SubmissionContainer.Submission> submissions = submissionContainer.submissions;
        StepikWrappers.MetricsWrapper metric = new StepikWrappers.MetricsWrapper(
                StepikWrappers.MetricsWrapper.PluginNames.STEPIK_UNION,
                StepikWrappers.MetricsWrapper.MetricActions.DOWNLOAD,
                targetTask.getLesson().getSection().getCourse().getId(),
                targetTask.getStepId());
        StepikConnectorPost.postMetric(metric);

        SupportedLanguages currentLang = targetTask.getCurrentLang();
        if (currentLang == null) {
            return;
        }
        String activateFileName = currentLang.getMainFileName();
        String code = null;
        for (StepikWrappers.SubmissionContainer.Submission submission : submissions) {
            if (submission.reply.language.startsWith(currentLang.getName())) {
                code = submission.reply.code;
                break;
            }
        }
        if (code == null) {
            return;
        }

        String mainFilePath = String.join("/", targetTask.getPath(), EduNames.SRC, activateFileName);
        VirtualFile mainFile = project.getBaseDir().findFileByRelativePath(mainFilePath);
        if (mainFile == null) {
            return;
        }

        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        final String finalCode = code;

        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(
                        () -> {
                            Document document = documentManager
                                    .getDocument(mainFile);
                            if (document != null) {
                                document.setText(finalCode);
                                FileEditorManager.getInstance(project).openFile(mainFile, true);
                            }
                        }),
                "Download last submission",
                "Download last submission");

    }

    @Override
    public void update(AnActionEvent e) {
        StudyUtils.updateAction(e);

        Project project = e.getProject();
        if (project == null) {
            return;
        }

        Task targetTask = StudyUtils.getSelectedTask(project);
        e.getPresentation().setEnabled(targetTask != null);
    }
}
