package org.stepik.plugin.actions;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.problems.WolfTheProblemSolver;
import com.jetbrains.tmp.learning.StudyState;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import icons.InteractiveLearningIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepikRefreshTaskFileAction extends StudyActionWithShortcut {
    private static final String ACTION_ID = "STEPIK.RefreshTaskAction";
    private static final String SHORTCUT = "ctrl shift pressed X";
    private static final Logger logger = Logger
            .getInstance(StepikRefreshTaskFileAction.class.getName());

    public StepikRefreshTaskFileAction() {
        super("Reset Task File (" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Refresh current task", InteractiveLearningIcons.ResetTaskFile);
    }

    private static void refresh(@NotNull final Project project) {
        ApplicationManager.getApplication()
                .invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
                    StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
                    StudyState studyState = new StudyState(studyEditor);
                    if (studyEditor == null || !studyState.isValid()) {
                        logger.info("RefreshTaskFileAction was invoked outside of Study Editor");
                        return;
                    }
                    refreshFile(studyState, project);
                }));
    }

    private static void refreshFile(
            @NotNull final StudyState studyState,
            @NotNull final Project project) {
        final Editor editor = studyState.getEditor();
        final TaskFile taskFile = studyState.getTaskFile();
        resetTaskFile(editor.getDocument(), project, taskFile);
        WolfTheProblemSolver.getInstance(project).clearProblems(studyState.getVirtualFile());
        ApplicationManager.getApplication().invokeLater(
                () -> IdeFocusManager.getInstance(project)
                        .requestFocus(editor.getContentComponent(), true));
        showBalloon(project, MessageType.INFO);
    }

    private static void resetTaskFile(
            @NotNull final Document document,
            @NotNull final Project project,
            TaskFile taskFile) {
        resetDocument(document, taskFile, project);
        taskFile.getTask().setStatus(StudyStatus.UNCHECKED);
        ProjectView.getInstance(project).refresh();
        StudyUtils.updateToolWindows(project);
    }

    private static void showBalloon(
            @NotNull final Project project,
            @NotNull final MessageType messageType) {
        BalloonBuilder balloonBuilder = JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("You can start again now", messageType, null);
        final Balloon balloon = balloonBuilder.createBalloon();
        StudyEditor selectedStudyEditor = StudyUtils.getSelectedStudyEditor(project);
        assert selectedStudyEditor != null;
        balloon.show(StudyUtils.computeLocation(selectedStudyEditor.getEditor()),
                Balloon.Position.above);
        Disposer.register(project, balloon);
    }

    private static void resetDocument(
            @NotNull final Document document,
            @NotNull final TaskFile taskFile,
            @NotNull Project project) {
        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager
                        .getApplication()
                        .runWriteAction(() -> document.setText(taskFile.getText())),
                "Stepik refresh task", "Stepik refresh task"
        );
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        if (project == null) {
            return;
        }

        refresh(project);
    }

    @Override
    public void update(AnActionEvent event) {
        StudyUtils.updateAction(event);

        final Project project = event.getProject();
        if (project == null) {
            return;
        }

        StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
        StudyState studyState = new StudyState(studyEditor);
        Presentation presentation = event.getPresentation();

        presentation.setEnabled(studyState.isValid());
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
}
