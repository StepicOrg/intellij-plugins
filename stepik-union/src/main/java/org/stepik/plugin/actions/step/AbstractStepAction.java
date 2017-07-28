package org.stepik.plugin.actions.step;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.actions.StudyActionWithShortcut;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;

import javax.swing.*;

/**
 * @author meanmail
 */
abstract class AbstractStepAction extends StudyActionWithShortcut {
    AbstractStepAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Nullable
    @Contract("null -> null")
    static StepNode getCurrentStep(@Nullable Project project) {
        StudyNode<?, ?> studyNode = StepikProjectManager.getSelected(project);
        return studyNode instanceof StepNode ? (StepNode) studyNode : null;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        StepNode stepNode = getCurrentStep(e.getProject());
        e.getPresentation().setEnabled(stepNode != null && !stepNode.getWasDeleted());
    }
}
