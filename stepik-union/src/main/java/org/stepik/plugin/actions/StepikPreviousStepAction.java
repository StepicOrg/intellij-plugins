package org.stepik.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.SectionNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.navigation.StudyNavigator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class StepikPreviousStepAction extends StepikStepNavigationAction {
    private static final String ACTION_ID = "STEPIK.PreviousStepAction";
    private static final String SHORTCUT = "ctrl pressed COMMA";

    public StepikPreviousStepAction() {
        super("Previous Step (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT),
                null)) + ")", "Navigate to the previous step", AllIcons.Actions.Back);
    }

    @Override
    protected StepNode getTargetStep(@NotNull final StepNode sourceStepNode) {
        return StudyNavigator.previousStep(sourceStepNode);
    }

    @Nullable
    @Override
    protected StepNode getDefaultStep(@NotNull final Project project) {
        CourseNode courseNode = StepikProjectManager.getInstance(project).getCourseNode();
        if (courseNode == null) {
            return null;
        }

        List<SectionNode> sectionNodes = courseNode.getSectionNodes();

        for (int i = sectionNodes.size() - 1; i >= 0; i--) {
            List<LessonNode> lessonNodes = sectionNodes.get(i).getLessonNodes();
            for (int j = lessonNodes.size() - 1; i >= 0; i--) {
                LessonNode lessonNode = lessonNodes.get(j);
                StepNode stepNode = lessonNode.getLastStep();
                if (stepNode != null) {
                    return stepNode;
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
