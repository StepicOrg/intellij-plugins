package org.stepik.plugin.actions.navigation;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.SectionNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepikNextStepAction extends StepikStepNavigationAction {
    private static final String ACTION_ID = "STEPIK.NextStepAction";
    private static final String SHORTCUT = "ctrl pressed PERIOD";

    public StepikNextStepAction() {
        super("Next Step (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT),
                null)) + ")", "Navigate to the next step", AllIcons.Actions.Forward);
    }

    @Override
    protected StepNode getTargetStep(@NotNull final StepNode sourceStepNode) {
        return StudyNavigator.nextStep(sourceStepNode);
    }

    @Nullable
    @Override
    protected StepNode getDefaultStep(@NotNull final Project project) {
        CourseNode courseNode = StepikProjectManager.getInstance(project).getCourseNode();
        if (courseNode == null) {
            return null;
        }

        for (SectionNode sectionNode : courseNode.getSectionNodes()) {
            for (LessonNode lessonNode : sectionNode.getLessonNodes()) {
                if (lessonNode.getStepNodes().size() > 0) {
                    return lessonNode.getStepNodes().get(0);
                }
            }
        }

        return null;
    }

    @NotNull
    @Override
    public String getActionId() {
        return ACTION_ID;
    }

    @Nullable
    @Override
    public String[] getShortcuts() {
        return new String[]{SHORTCUT};
    }
}
