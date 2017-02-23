package org.stepik.plugin.actions.step;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.problems.WolfTheProblemSolver;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.metrics.MetricsStatus;
import org.stepik.core.utils.ProjectFilesUtils;

import javax.swing.*;

public class StepikResetStepAction extends AbstractStepAction {
    private static final String ACTION_ID = "STEPIK.ResetStepAction";
    private static final String SHORTCUT = "ctrl shift pressed X";

    public StepikResetStepAction() {
        super("Reset Step File (" + KeymapUtil.getShortcutText(
                new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Reset current step", AllStepikIcons.ToolWindow.resetTaskFile);
    }

    private static void reset(@NotNull final Project project) {
        ApplicationManager.getApplication()
                .invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> resetFile(project)));
    }

    private static void resetFile(@NotNull final Project project) {
        StepNode stepNode = StudyUtils.getSelectedStep(project);
        if (stepNode == null) {
            return;
        }

        VirtualFile src = ProjectFilesUtils.getOrCreateSrcDirectory(project, stepNode);
        if (src == null) {
            return;
        }

        String mainFileName = stepNode.getCurrentLang().getMainFileName();
        VirtualFile mainFile = src.findChild(mainFileName);

        if (mainFile != null) {
            FileDocumentManager documentManager = FileDocumentManager.getInstance();
            Document document = documentManager.getDocument(mainFile);
            if (document != null) {
                resetDocument(project, document, stepNode);
                stepNode.setStatus(StudyStatus.UNCHECKED);
                ProjectView.getInstance(project).refresh();
                StudyUtils.updateToolWindows(project);
                WolfTheProblemSolver.getInstance(project).clearProblems(mainFile);
            }
        }
    }

    private static void resetDocument(
            @NotNull Project project,
            @NotNull final Document document,
            @NotNull final StepNode stepNode) {
        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager
                        .getApplication()
                        .runWriteAction(() -> {
                            document.setText(stepNode.getCurrentTemplate());
                            Metrics.resetStepAction(project, stepNode, MetricsStatus.SUCCESSFUL);
                        }),
                "Stepik reset step", "Stepik reset step"
        );
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        if (project == null) {
            return;
        }

        reset(project);
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
