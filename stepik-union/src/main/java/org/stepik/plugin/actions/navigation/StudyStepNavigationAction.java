package org.stepik.plugin.actions.navigation;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


abstract class StudyStepNavigationAction extends StudyActionWithShortcut {
    StudyStepNavigationAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    protected abstract void navigateStep(@NotNull final Project project);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        navigateStep(project);
    }

    protected abstract StudyNode getTargetStep(@Nullable final StudyNode sourceStepNode);

    @Override
    public void update(AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        presentation.setEnabled(false);

        Project project = e.getProject();
        if (!StepikProjectManager.isStepikProject(project)) {
            return;
        }

        StudyNode stepNode = StudyUtils.getSelectedNodeInTree(project);
        presentation.setEnabled(stepNode == null || getTargetStep(stepNode) != null);
    }
}
