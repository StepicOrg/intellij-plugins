package org.stepik.plugin.actions.step;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.courseFormat.StepNode;

import javax.swing.*;

import static org.stepik.core.courseFormat.StepType.CODE;

abstract class CodeQuizAction extends AbstractStepAction {
    CodeQuizAction(
            @Nullable String text,
            @Nullable String description,
            @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Nullable
    @Contract("null -> null")
    static StepNode getCurrentCodeStepNode(@Nullable Project project) {
        StepNode stepNode = getCurrentStep(project);
        if (stepNode == null || stepNode.getType() != CODE) {
            return null;
        }
        return stepNode;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();

        if (!presentation.isEnabled()) {
            return;
        }

        StepNode stepNode = getCurrentStep(e.getProject());
        presentation.setEnabled((stepNode != null) && (stepNode.getType() == CODE));
    }
}
