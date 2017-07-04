package org.stepik.plugin.actions.step;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.submissions.Submission;
import org.stepik.api.objects.submissions.Submissions;
import org.stepik.api.queries.Order;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.courseFormat.StudyStatus;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.utils.Utils;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.stepik.core.SupportedLanguages.Companion;
import static org.stepik.core.metrics.MetricsStatus.DATA_NOT_LOADED;
import static org.stepik.core.metrics.MetricsStatus.EMPTY_SOURCE;
import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.metrics.MetricsStatus.TARGET_NOT_FOUND;
import static org.stepik.core.metrics.MetricsStatus.USER_CANCELED;
import static org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient;
import static org.stepik.core.stepik.StepikAuthManager.getCurrentUser;
import static org.stepik.core.stepik.StepikAuthManager.isAuthenticated;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;
import static org.stepik.plugin.utils.DirectivesUtilsKt.containsDirectives;
import static org.stepik.plugin.utils.DirectivesUtilsKt.replaceCode;
import static org.stepik.plugin.utils.DirectivesUtilsKt.uncommentAmbientCode;

/**
 * @author meanmail
 * @since 0.8
 */
public class DownloadSubmission extends CodeQuizAction {
    private static final Logger logger = Logger.getInstance(DownloadSubmission.class);
    private static final String ACTION_ID = "STEPIK.DownloadSubmission";
    private static final String SHORTCUT = "ctrl alt pressed PAGE_DOWN";

    public DownloadSubmission() {
        super("Download submission from the List(" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Download submission from the List", AllStepikIcons.ToolWindow.download);
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
        ApplicationManager.getApplication()
                .executeOnPooledThread(() -> downloadSubmission(e.getProject()));
    }

    private void downloadSubmission(@Nullable Project project) {
        if (project == null) {
            return;
        }

        StudyNode<?, ?> studyNode = StepikProjectManager.getSelected(project);
        if (!(studyNode instanceof StepNode)) {
            return;
        }

        StepNode stepNode = (StepNode) studyNode;

        String title = "Download submission";

        StepikApiClient stepikApiClient = authAndGetStepikApiClient(true);
        if (!isAuthenticated()) {
            return;
        }

        List<Submission> submissions = ProgressManager.getInstance()
                .run(new Task.WithResult<List<Submission>, RuntimeException>(project, title, true) {
                    @Override
                    protected List<Submission> compute(@NotNull ProgressIndicator progressIndicator)
                            throws RuntimeException {
                        progressIndicator.setIndeterminate(true);
                        StudyNode parent = stepNode.getParent();
                        String lessonName = parent != null ? parent.getName() : "";
                        progressIndicator.setText(lessonName);
                        progressIndicator.setText2(stepNode.getName());
                        List<Submission> submissions = getSubmissions(stepikApiClient, stepNode);

                        if (Utils.isCanceled()) {
                            Metrics.downloadAction(project, stepNode, USER_CANCELED);
                            return null;
                        }

                        if (submissions == null) {
                            Metrics.downloadAction(project, stepNode, DATA_NOT_LOADED);
                            return Collections.emptyList();
                        }

                        SupportedLanguages currentLang = stepNode.getCurrentLang();

                        return filterSubmissions(submissions, currentLang);
                    }
                });

        if (submissions == null) {
            return;
        }

        ApplicationManager.getApplication().invokeAndWait(() ->
                showPopup(project, stepNode, submissions)
        );
    }

    @Nullable
    private List<Submission> getSubmissions(
            @NotNull StepikApiClient stepikApiClient,
            @NotNull StepNode stepNode) {
        try {
            long stepId = stepNode.getId();
            long userId = getCurrentUser().getId();

            Submissions submissions = stepikApiClient.submissions()
                    .get()
                    .step(stepId)
                    .user(userId)
                    .order(Order.DESC)
                    .execute();

            return submissions.getSubmissions();
        } catch (StepikClientException e) {
            logger.warn("Failed get submissions", e);
            return null;
        }
    }

    @NotNull
    private List<Submission> filterSubmissions(
            @NotNull List<Submission> submissions,
            @NotNull SupportedLanguages currentLang) {
        return submissions.stream()
                .filter(submission -> {
                    String languageName = submission.getReply().getLanguage();
                    return Companion.langOfName(languageName).upgradedTo(currentLang);
                })
                .collect(Collectors.toList());
    }

    private void showPopup(
            @NotNull Project project,
            @NotNull StepNode stepNode,
            @NotNull List<Submission> submissions) {
        JBPopupFactory popupFactory = JBPopupFactory.getInstance();

        PopupChooserBuilder builder;
        if (!submissions.isEmpty()) {
            JList<SubmissionDecorator> list;

            List<SubmissionDecorator> submissionDecorators = submissions.stream()
                    .map(SubmissionDecorator::new).collect(Collectors.toList());
            list = new JList<>(submissionDecorators.toArray(new SubmissionDecorator[submissionDecorators.size()]));
            builder = popupFactory.createListPopupBuilder(list)
                    .addListener(new Listener(list, project, stepNode));
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
            @NotNull StepNode stepNode,
            @NotNull Submission submission) {

        String fileName = stepNode.getCurrentLang().getMainFileName();

        VirtualFile src = getOrCreateSrcDirectory(project, stepNode, true);
        if (src == null) {
            Metrics.downloadAction(project, stepNode, TARGET_NOT_FOUND);
            return;
        }

        VirtualFile mainFile = src.findChild(fileName);
        if (mainFile == null) {
            Metrics.downloadAction(project, stepNode, TARGET_NOT_FOUND);
            return;
        }

        final String finalCode = submission.getReply().getCode();

        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(
                        () -> {
                            FileDocumentManager documentManager = FileDocumentManager.getInstance();
                            Document document = documentManager.getDocument(mainFile);

                            if (document != null) {
                                SupportedLanguages language = Companion.langOfName(submission.getReply().getLanguage());
                                if (containsDirectives(finalCode, language)) {
                                    String text = uncommentAmbientCode(finalCode, language);
                                    document.setText(text);
                                } else {
                                    String code = replaceCode(document.getText(), finalCode, language);
                                    document.setText(code);
                                }

                                StudyStatus status = StudyStatus.of(submission.getStatus());
                                stepNode.setStatus(status);
                                FileEditorManager.getInstance(project).openFile(mainFile, true);
                                ProjectView.getInstance(project).refresh();
                                Metrics.downloadAction(project, stepNode, SUCCESSFUL);
                            }
                        }),
                "Download submission",
                "Download submission");
    }

    private static class SubmissionDecorator {
        private final static SimpleDateFormat timeOutFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        private final Submission submission;

        SubmissionDecorator(Submission submission) {
            this.submission = submission;
        }

        @Override
        public String toString() {
            Date utcTime = submission.getTime();
            String localTime = timeOutFormat.format(utcTime);

            return String.format("#%d %-7s %s", submission.getId(), submission.getStatus(), localTime);
        }

        Submission getSubmission() {
            return submission;
        }
    }

    private class Listener implements JBPopupListener {
        private final JList<SubmissionDecorator> list;
        private final Project project;
        private final StepNode stepNode;

        Listener(
                @NotNull JList<SubmissionDecorator> list,
                @NotNull Project project,
                @NotNull StepNode stepNode) {
            this.list = list;
            this.project = project;
            this.stepNode = stepNode;
        }

        @Override
        public void beforeShown(LightweightWindowEvent event) {
        }

        @Override
        public void onClosed(LightweightWindowEvent event) {
            if (!event.isOk()) {
                Metrics.downloadAction(project, stepNode, USER_CANCELED);
                return;
            } else if (list.isSelectionEmpty()) {
                Metrics.downloadAction(project, stepNode, EMPTY_SOURCE);
                return;
            }

            Submission submission = list.getSelectedValue().getSubmission();

            if (submission == null) {
                Metrics.downloadAction(project, stepNode, EMPTY_SOURCE);
                return;
            }

            loadSubmission(project, stepNode, submission);
        }
    }
}