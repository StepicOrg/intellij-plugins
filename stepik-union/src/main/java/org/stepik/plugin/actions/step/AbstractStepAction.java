package org.stepik.plugin.actions.step;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
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
        final Presentation presentation = e.getPresentation();
        presentation.setEnabled(false);

        Project project = e.getProject();
        if (project == null) {
            return;
        }

        StepNode targetStepNode = StudyUtils.getSelectedStep(project);
        presentation.setEnabled(targetStepNode != null);
    }
}
