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
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.problems.WolfTheProblemSolver;
import com.jetbrains.tmp.learning.StudyActionListener;
import com.jetbrains.tmp.learning.StudyState;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.core.EduAnswerPlaceholderPainter;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import com.jetbrains.tmp.learning.navigation.StudyNavigator;
import icons.InteractiveLearningIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepikRefreshTaskFileAction extends StudyActionWithShortcut {
    private static final String ACTION_ID = "STEPIK.RefreshTaskAction";
    private static final String SHORTCUT = "ctrl shift pressed X";
    private static final Logger LOG = Logger
            .getInstance(StepikRefreshTaskFileAction.class.getName());

    public StepikRefreshTaskFileAction() {
        super("Reset Task File (" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Refresh current task", InteractiveLearningIcons.ResetTaskFile);
    }

    public static void refresh(@NotNull final Project project) {
        ApplicationManager.getApplication()
                .invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
                    StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
                    StudyState studyState = new StudyState(studyEditor);
                    if (studyEditor == null || !studyState.isValid()) {
                        LOG.info("RefreshTaskFileAction was invoked outside of Study Editor");
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
        if (!resetTaskFile(editor.getDocument(), project, taskFile,
                studyState.getVirtualFile().getName())) {
            Messages.showInfoMessage("The initial text of task file is unavailable",
                    "Failed to Refresh Task File");
            return;
        }
        WolfTheProblemSolver.getInstance(project).clearProblems(studyState.getVirtualFile());
        taskFile.setHighlightErrors(false);
        StudyUtils.drawAllWindows(editor, taskFile);
        EduAnswerPlaceholderPainter.createGuardedBlocks(editor, taskFile);
        ApplicationManager.getApplication().invokeLater(
                () -> IdeFocusManager.getInstance(project)
                        .requestFocus(editor.getContentComponent(), true));

        StudyNavigator.navigateToFirstAnswerPlaceholder(editor, taskFile);
        showBalloon(project, "You can start again now", MessageType.INFO);
    }

    private static boolean resetTaskFile(
            @NotNull final Document document,
            @NotNull final Project project,
            TaskFile taskFile,
            String name) {
        if (!resetDocument(document, taskFile, name, project)) {
            return false;
        }
        taskFile.getTask().setStatus(StudyStatus.Unchecked);
        ProjectView.getInstance(project).refresh();
        StudyUtils.updateToolWindows(project);
        return true;
    }

    private static void showBalloon(
            @NotNull final Project project, String text,
            @NotNull final MessageType messageType) {
        BalloonBuilder balloonBuilder = JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, messageType, null);
        final Balloon balloon = balloonBuilder.createBalloon();
        StudyEditor selectedStudyEditor = StudyUtils.getSelectedStudyEditor(project);
        assert selectedStudyEditor != null;
        balloon.show(StudyUtils.computeLocation(selectedStudyEditor.getEditor()),
                Balloon.Position.above);
        Disposer.register(project, balloon);
    }

    private static boolean resetDocument(
            @NotNull final Document document,
            @NotNull final TaskFile taskFile,
            String fileName,
            @NotNull Project project) {
        final Document patternDocument = StudyUtils.getPatternDocument(taskFile, fileName);
        if (patternDocument == null) {
            return false;
        }
        StudyUtils.deleteGuardedBlocks(document);

        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager
                        .getApplication()
                        .runWriteAction(() -> document.setText(patternDocument.getCharsSequence())),
                "Stepik refresh task", "Stepik refresh task"
        );
        return true;
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        if (project == null) {
            return;
        }
        for (StudyActionListener listener : Extensions.getExtensions(StudyActionListener.EP_NAME)) {
            listener.beforeCheck(event);
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
        if (!studyState.isValid()) {
            presentation.setEnabled(false);
            return;
        }

        Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null) {
            return;
        }

        presentation.setVisible(true);
        String courseMode = course.getCourseMode();
        if (!(EduNames.STEPIK_CODE.equals(courseMode))) {
            presentation.setEnabled(false);
        }
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
