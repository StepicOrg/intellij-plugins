package org.stepik.plugin.actions.navigation;

import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.plugin.utils.NavigationUtils;

import javax.swing.*;

abstract class StepikStepNavigationAction extends StudyStepNavigationAction {
    StepikStepNavigationAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void navigateStep(@NotNull final Project project) {
        StudyNode currentNode = StepikProjectManager.getSelected(project);
        if (currentNode == null) {
            currentNode = StepikProjectManager.getProjectRoot(project);
        }

        StudyNode targetNode;

        targetNode = getTargetStep(currentNode);

        if (targetNode == null) {
            return;
        }

        NavigationUtils.navigate(project, targetNode);
    }
}
