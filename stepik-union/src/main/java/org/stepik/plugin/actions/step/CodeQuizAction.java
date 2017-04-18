package org.stepik.plugin.actions.step;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;

import javax.swing.*;

import static org.stepik.core.courseFormat.StepType.CODE;

abstract class CodeQuizAction extends AbstractStepAction {
    CodeQuizAction(
            @Nullable String text,
            @Nullable String description,
            @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        StudyNode<?, ?> selectedNode = StepikProjectManager.getSelected(e.getProject());
        boolean enabled = presentation.isEnabled();
        boolean canEnabled = (selectedNode instanceof StepNode) && (((StepNode) selectedNode).getType() == CODE);
        presentation.setEnabled(enabled && canEnabled);
    }
}
