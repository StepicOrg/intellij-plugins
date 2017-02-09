package org.stepik.plugin.actions.navigation;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.metrics.Metrics;

import javax.swing.*;

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

        VirtualFile projectDir = project.getBaseDir();
        if (projectDir == null) {
            return;
        }

        VirtualFile srcDir = projectDir.findFileByRelativePath(targetStepNode.getPath() + "/" + EduNames.SRC);
        if (srcDir == null) {
            return;
        }

        VirtualFile mainFile = srcDir.findChild(targetStepNode.getCurrentLang().getMainFileName());
        if (mainFile == null) {
            mainFile = srcDir;
        }

        updateProjectView(project, mainFile);
        Metrics.navigateAction(project, targetStepNode, SUCCESSFUL);

        ToolWindow runToolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.RUN);
        if (runToolWindow != null) {
            runToolWindow.hide(null);
        }
    }
}
