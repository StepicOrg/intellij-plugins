package org.stepik.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.LangManager;
import com.jetbrains.tmp.learning.LangSetting;
import com.jetbrains.tmp.learning.StudyState;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import com.jetbrains.tmp.learning.stepik.metric.MetricActions;
import com.jetbrains.tmp.learning.stepik.metric.MetricBuilder;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import com.jetbrains.tmp.learning.stepik.StepikConnectorPost;
import com.jetbrains.tmp.learning.stepik.StepikWrappers;
import com.jetbrains.tmp.learning.stepik.metric.MetricsWrapper;
import com.jetbrains.tmp.learning.stepik.metric.PluginNames;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.jetbrains.tmp.learning.stepik.SupportedLanguages;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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

    private void downloadSubmission(Project project) {
        StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
        StudyState studyState = new StudyState(studyEditor);
        if (!studyState.isValid()) {
            return;
        }
        Task targetTask = studyState.getTask();
        if (targetTask == null) {
            return;
        }

        String stepId = Integer.toString(targetTask.getStepId());
        String userId = Integer.toString(StudyTaskManager.getInstance(project).getUser().getId());

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("step", stepId));
        nvps.add(new BasicNameValuePair("user", userId));
        nvps.add(new BasicNameValuePair("order", "desc"));

        LangManager langManager = StudyTaskManager.getInstance(project).getLangManager();
        LangSetting langSetting = langManager.getLangSetting(targetTask);
        SupportedLanguages currentLang = SupportedLanguages.langOf(langSetting.getCurrentLang());
        if (currentLang == null) {
            return;
        }

        StepikWrappers.SubmissionContainer submissionContainer = StepikConnectorGet.getSubmissions(nvps);
        if (submissionContainer == null) {
            return;
        }
        List<StepikWrappers.SubmissionContainer.Submission> submissions = submissionContainer.submissions;
        MetricsWrapper metric = new MetricBuilder()
                .addTag(PluginNames.STEPIK_UNION)
                .addTag(MetricActions.DOWNLOAD)
                .addTag(currentLang)
                .setCourseId(targetTask.getLesson().getSection().getCourse().getId())
                .setStepId(targetTask.getStepId())
                .build();
        StepikConnectorPost.postMetric(metric);


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
        final String finalCode = code;

        VirtualFile vf = studyState.getTaskDir().findChild(activateFileName);
        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        if (vf == null) {
            return;
        }
        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(
                        () -> {
                            Document document = documentManager
                                    .getDocument(vf);
                            if (document != null)
                                document.setText(finalCode);
                        }),
                "Download last submission",
                "Download last submission");

    }
}
