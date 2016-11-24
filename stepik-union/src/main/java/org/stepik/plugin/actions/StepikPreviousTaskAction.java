package org.stepik.plugin.actions;

import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.navigation.StudyNavigator;
import icons.InteractiveLearningIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class StepikPreviousTaskAction extends StepikTaskNavigationAction {
    private static final String ACTION_ID = "STEPIK.PreviousTaskAction";
    private static final String SHORTCUT = "ctrl pressed COMMA";

    public StepikPreviousTaskAction() {
        super("Previous Task (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT),
                null)) + ")", "Navigate to the previous task", InteractiveLearningIcons.Prev);
    }

    @Override
    protected Task getTargetTask(@NotNull final Task sourceTask) {
        return StudyNavigator.previousTask(sourceTask);
    }

    @Nullable
    @Override
    protected Task getDefaultTask(@NotNull final Project project) {
        Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null) {
            return null;
        }

        List<Section> sections = course.getSections();

        for (int i = sections.size() - 1; i >= 0; i--) {
            List<Lesson> lessons = sections.get(i).getLessons();
            for (int j = lessons.size() - 1; i >= 0; i--) {
                Lesson lesson = lessons.get(j);
                List<Task> tasks = lesson.getTaskList();
                if (tasks.size() > 0) {
                    return lesson.getTaskList().get(tasks.size() - 1);
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
