package org.stepik.plugin.actions.step;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
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
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepikResetStepAction extends AbstractStepAction {
    private static final String ACTION_ID = "STEPIK.ResetStepAction";
    private static final String SHORTCUT = "ctrl shift pressed X";
    private static final Logger logger = Logger
            .getInstance(StepikResetStepAction.class.getName());

    public StepikResetStepAction() {
        super("Reset Step File (" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Reset current step", AllStepikIcons.ToolWindow.resetTaskFile);
    }

    private static void refresh(@NotNull final Project project) {
        ApplicationManager.getApplication()
                .invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
                    StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
                    StudyState studyState = new StudyState(studyEditor);
                    if (studyEditor == null || !studyState.isValid()) {
                        logger.info("ResetStepAction was invoked outside of Study Editor");
                        return;
                    }
                    refreshFile(studyState, project);
                }));
    }

    private static void refreshFile(
            @NotNull final StudyState studyState,
            @NotNull final Project project) {
        final Editor editor = studyState.getEditor();
        final StepFile stepFile = studyState.getStepFile();
        resetStepFile(editor.getDocument(), project, stepFile);
        WolfTheProblemSolver.getInstance(project).clearProblems(studyState.getVirtualFile());
        ApplicationManager.getApplication().invokeLater(
                () -> IdeFocusManager.getInstance(project)
                        .requestFocus(editor.getContentComponent(), true));
        showBalloon(project);
    }

    private static void resetStepFile(
            @NotNull final Document document,
            @NotNull final Project project,
            StepFile stepFile) {
        resetDocument(document, stepFile, project);
        StepNode stepNode = stepFile.getStepNode();
        if (stepNode != null) {
            stepNode.setStatus(StudyStatus.UNCHECKED);
        }
        ProjectView.getInstance(project).refresh();
        StudyUtils.updateToolWindows(project);
    }

    private static void showBalloon(
            @NotNull final Project project) {
        BalloonBuilder balloonBuilder = JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("You can start again now", MessageType.INFO, null);
        final Balloon balloon = balloonBuilder.createBalloon();
        StudyEditor selectedStudyEditor = StudyUtils.getSelectedStudyEditor(project);
        assert selectedStudyEditor != null;
        balloon.show(StudyUtils.computeLocation(selectedStudyEditor.getEditor()),
                Balloon.Position.above);
        Disposer.register(project, balloon);
    }

    private static void resetDocument(
            @NotNull final Document document,
            @NotNull final StepFile stepFile,
            @NotNull Project project) {
        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager
                        .getApplication()
                        .runWriteAction(() -> document.setText(stepFile.getText())),
                "Stepik refresh step", "Stepik refresh step"
        );
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        if (project == null) {
            return;
        }

        refresh(project);
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
