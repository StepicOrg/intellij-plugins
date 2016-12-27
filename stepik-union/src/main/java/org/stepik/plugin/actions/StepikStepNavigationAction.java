package org.stepik.plugin.actions;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyStepNavigationAction;
import com.jetbrains.tmp.learning.courseFormat.Step;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public abstract class StepikStepNavigationAction extends StudyStepNavigationAction {
    StepikStepNavigationAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Nullable
    protected abstract Step getDefaultStep(@NotNull final Project project);

    @Override
    public void navigateStep(@NotNull final Project project) {
        Step currentStep = StudyUtils.getSelectedStep(project);
        Step targetStep;

        if (currentStep == null) {
            targetStep = getDefaultStep(project);
        } else {
            targetStep = getTargetStep(currentStep);
        }

        if (targetStep == null) {
            return;
        }
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            FileEditorManager.getInstance(project).closeFile(file);
        }
        Map<String, StepFile> nextStepFiles = targetStep.getStepFiles();
        VirtualFile projectDir = project.getBaseDir();
        if (projectDir == null) {
            return;
        }

        VirtualFile stepDir = projectDir.findFileByRelativePath(targetStep.getPath());
        if (stepDir == null) {
            return;
        }
        if (nextStepFiles.isEmpty()) {
            ProjectView.getInstance(project).select(stepDir, stepDir, false);
            return;
        }
        VirtualFile shouldBeActive = getFileToActivate(project, nextStepFiles, stepDir);

        updateProjectView(project, shouldBeActive);

        ToolWindow runToolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.RUN);
        if (runToolWindow != null) {
            runToolWindow.hide(null);
        }
    }
}
