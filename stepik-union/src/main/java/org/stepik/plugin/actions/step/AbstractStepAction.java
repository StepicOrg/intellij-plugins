package org.stepik.plugin.actions.step;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.actions.StudyActionWithShortcut;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author meanmail
 */
abstract class AbstractStepAction extends StudyActionWithShortcut {
    AbstractStepAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void update(AnActionEvent e) {
        StudyNode<?, ?> targetStepNode = StepikProjectManager.getSelected(e.getProject());
        e.getPresentation().setEnabled((targetStepNode instanceof StepNode) && !targetStepNode.getWasDeleted());
    }
}
