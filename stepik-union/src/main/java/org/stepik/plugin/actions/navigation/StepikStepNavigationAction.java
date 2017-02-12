package org.stepik.plugin.actions.navigation;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.metrics.Metrics;

import javax.swing.*;
import java.util.Map;

import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;

abstract class StepikStepNavigationAction extends StudyStepNavigationAction {
    StepikStepNavigationAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Nullable
    protected abstract StepNode getDefaultStep(@NotNull final Project project);

    @Override
    public void navigateStep(@NotNull final Project project) {
        StepNode currentStepNode = StudyUtils.getSelectedStep(project);
        StepNode targetStepNode;

        if (currentStepNode == null) {
            targetStepNode = getDefaultStep(project);
        } else {
            targetStepNode = getTargetStep(currentStepNode);
        }

        if (targetStepNode == null) {
            return;
        }
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            FileEditorManager.getInstance(project).closeFile(file);
        }
        Map<String, StepFile> nextStepFiles = targetStepNode.getStepFiles();
        VirtualFile projectDir = project.getBaseDir();
        if (projectDir == null) {
            return;
        }

        VirtualFile stepDir = projectDir.findFileByRelativePath(targetStepNode.getPath());
        if (stepDir == null) {
            return;
        }
        if (nextStepFiles.isEmpty()) {
            ProjectView.getInstance(project).select(stepDir, stepDir, false);
            return;
        }
        VirtualFile shouldBeActive = getFileToActivate(project, nextStepFiles, stepDir);

        updateProjectView(project, shouldBeActive);
        Metrics.navigateAction(project, targetStepNode, SUCCESSFUL);

        ToolWindow runToolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.RUN);
        if (runToolWindow != null) {
            runToolWindow.hide(null);
        }
    }
}
