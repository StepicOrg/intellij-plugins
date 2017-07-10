package org.stepik.plugin.actions.step;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.problems.WolfTheProblemSolver;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.metrics.MetricsStatus;
import org.stepik.plugin.actions.ActionUtils;

import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;

public class StepikResetStepAction extends CodeQuizAction {
    private static final String ACTION_ID = "STEPIK.ResetStepAction";
    private static final String SHORTCUT = "ctrl shift pressed X";
    private static final String SHORTCUT_TEXT = ActionUtils.getShortcutText(SHORTCUT);
    private static final String TEXT = "Reset Step File (" + SHORTCUT_TEXT + ")";
    private static final String DESCRIPTION = "Reset current step";

    public StepikResetStepAction() {
        super(TEXT, DESCRIPTION, AllStepikIcons.ToolWindow.resetTaskFile);
    }

    private static void reset(@Nullable final Project project) {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() ->
                application.runWriteAction(() -> resetFile(project)));
    }

    private static void resetFile(@Nullable final Project project) {
        StepNode stepNode = getCurrentCodeStepNode(project);
        if (stepNode == null) {
            return;
        }

        VirtualFile src = getOrCreateSrcDirectory(project, stepNode, true);
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
                if (!project.isDisposed()) {
                    ProjectView.getInstance(project).refresh();
                    WolfTheProblemSolver.getInstance(project).clearProblems(mainFile);
                }
                StepikProjectManager.updateSelection(project);
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

                            stepNode.getCurrentLang().getTestRunner().updateRunConfiguration(project, stepNode);
                        }),
                "Stepik reset step", "Stepik reset step"
        );
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        reset(event.getProject());
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
