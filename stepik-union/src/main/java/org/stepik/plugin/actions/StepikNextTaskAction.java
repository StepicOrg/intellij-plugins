package org.stepik.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.navigation.StudyNavigator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepikNextTaskAction extends StepikTaskNavigationAction {
    private static final String ACTION_ID = "STEPIK.NextTaskAction";
    private static final String SHORTCUT = "ctrl pressed PERIOD";

    public StepikNextTaskAction() {
        super("Next Task (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT),
                null)) + ")", "Navigate to the next task", AllIcons.Actions.Forward);
    }

    @Override
    protected Task getTargetTask(@NotNull final Task sourceTask) {
        return StudyNavigator.nextTask(sourceTask);
    }

    @Nullable
    @Override
    protected Task getDefaultTask(@NotNull final Project project) {
        Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null) {
            return null;
        }

        for (Section section : course.getSections()) {
            for (Lesson lesson : section.getLessons()) {
                if (lesson.getTaskList().size() > 0) {
                    return lesson.getTaskList().get(0);
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
