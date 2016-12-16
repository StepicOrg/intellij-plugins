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
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
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
import com.jetbrains.tmp.learning.stepik.entities.Submission;
import com.jetbrains.tmp.learning.stepik.entities.SubmissionContainer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author meanmail
 * @since 0.8
 */
public class DownloadSubmission extends StudyActionWithShortcut {
    private static final String ACTION_ID = "STEPIK.DownloadSubmission";
    private static final String SHORTCUT = "ctrl alt pressed PAGE_DOWN";

    public DownloadSubmission() {
        super("Download submission from the List(" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Download submission from the List", IconLoader.getIcon("/icons/arrow-down.png"));
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

        Task task = StudyUtils.getSelectedTask(project);
        if (task == null) {
            return;
        }

        List<Submission> submissions = getSubmissions(project, task);

        if (submissions == null) {
            return;
        }

        SupportedLanguages currentLang = task.getCurrentLang();

        submissions = filterSubmissions(submissions, currentLang);

        showPopup(project, task, submissions, currentLang);
    }

    @Nullable
    private List<Submission> getSubmissions(
            @NotNull Project project,
            @NotNull Task task) {
        String stepId = Integer.toString(task.getStepId());
        String userId = Integer.toString(StudyTaskManager.getInstance(project).getUser().getId());

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("step", stepId));
        nvps.add(new BasicNameValuePair("user", userId));
        nvps.add(new BasicNameValuePair("order", "desc"));

        SubmissionContainer submissionContainer = StepikConnectorGet.getSubmissions(nvps);

        if (submissionContainer == null) {
            return null;
        }

        return submissionContainer.getSubmissions();
    }

    @NotNull
    private List<Submission> filterSubmissions(
            @NotNull List<Submission> submissions,
            @NotNull SupportedLanguages currentLang) {
        final String langName = currentLang.getName();
        return submissions.stream()
                .filter(submission -> submission.getReply().getLanguage().startsWith(langName))
                .collect(Collectors.toList());
    }

    private void showPopup(
            @NotNull Project project,
            @NotNull Task task,
            @NotNull List<Submission> submissions,
            @NotNull SupportedLanguages currentLang) {
        JBPopupFactory popupFactory = JBPopupFactory.getInstance();

        PopupChooserBuilder builder;
        if (submissions.size() > 0) {
            JList<Submission> list;
            list = new JList<>(submissions.toArray(new Submission[submissions.size()]));
            builder = popupFactory.createListPopupBuilder(list)
                    .addListener(new Listener(list, project, task, currentLang));
        } else {
            JList<String> emptyList = new JList<>(new String[]{"Empty"});
            builder = popupFactory.createListPopupBuilder(emptyList);
        }

        builder = builder.setTitle("Choose submission");

        JBPopup popup = builder.createPopup();

        popup.showCenteredInCurrentWindow(project);
    }

    private void loadSubmission(
            @NotNull Project project,
            @NotNull SupportedLanguages currentLang,
            @NotNull Task task,
            @NotNull Submission submission) {

        String fileName = currentLang.getMainFileName();

        String mainFilePath = String.join("/", task.getPath(), EduNames.SRC, fileName);
        VirtualFile mainFile = project.getBaseDir().findFileByRelativePath(mainFilePath);
        if (mainFile == null) {
            return;
        }

        final String finalCode = submission.getReply().getCode();

        StepikWrappers.MetricsWrapper metric = new StepikWrappers.MetricsWrapper(
                StepikWrappers.MetricsWrapper.PluginNames.STEPIK_UNION,
                StepikWrappers.MetricsWrapper.MetricActions.DOWNLOAD,
                task.getLesson().getSection().getCourse().getId(),
                task.getStepId());
        StepikConnectorPost.postMetric(metric);

        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(
                        () -> {
                            FileDocumentManager documentManager = FileDocumentManager.getInstance();
                            Document document = documentManager.getDocument(mainFile);

                            if (document != null) {
                                document.setText(finalCode);
                                FileEditorManager.getInstance(project).openFile(mainFile, true);
                            }
                        }),
                "Download submission",
                "Download submission");
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

    private class Listener implements JBPopupListener {
        private final JList<Submission> list;
        private final Project project;
        private final Task task;
        private final SupportedLanguages currentLang;

        Listener(
                @NotNull JList<Submission> list,
                @NotNull Project project,
                @NotNull Task task,
                @NotNull SupportedLanguages currentLang) {
            this.list = list;
            this.project = project;
            this.task = task;
            this.currentLang = currentLang;
        }

        @Override
        public void beforeShown(LightweightWindowEvent event) {
        }

        @Override
        public void onClosed(LightweightWindowEvent event) {
            if (!event.isOk()) {
                return;
            }

            Submission submission = list.getSelectedValue();

            if (submission == null) {
                return;
            }

            loadSubmission(project, currentLang, task, submission);
        }
    }
}