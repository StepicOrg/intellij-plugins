package org.stepik.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.Step;
import com.jetbrains.tmp.learning.navigation.StudyNavigator;
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
    protected Step getTargetStep(@NotNull final Step sourceStep) {
        return StudyNavigator.nextStep(sourceStep);
    }

    @Nullable
    @Override
    protected Step getDefaultStep(@NotNull final Project project) {
        Course course = StepikProjectManager.getInstance(project).getCourse();
        if (course == null) {
            return null;
        }

        for (Section section : course.getSections()) {
            for (Lesson lesson : section.getLessons()) {
                if (lesson.getStepList().size() > 0) {
                    return lesson.getStepList().get(0);
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
