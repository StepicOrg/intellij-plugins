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
import java.util.List;

public class StepikPreviousStepAction extends StepikStepNavigationAction {
    private static final String ACTION_ID = "STEPIK.PreviousStepAction";
    private static final String SHORTCUT = "ctrl pressed COMMA";

    public StepikPreviousStepAction() {
        super("Previous Step (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT),
                null)) + ")", "Navigate to the previous step", AllIcons.Actions.Back);
    }

    @Override
    protected Step getTargetStep(@NotNull final Step sourceStep) {
        return StudyNavigator.previousStep(sourceStep);
    }

    @Nullable
    @Override
    protected Step getDefaultStep(@NotNull final Project project) {
        Course course = StepikProjectManager.getInstance(project).getCourse();
        if (course == null) {
            return null;
        }

        List<Section> sections = course.getSections();

        for (int i = sections.size() - 1; i >= 0; i--) {
            List<Lesson> lessons = sections.get(i).getLessons();
            for (int j = lessons.size() - 1; i >= 0; i--) {
                Lesson lesson = lessons.get(j);
                Step step = lesson.getLastStep();
                if (step != null) {
                    return step;
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
