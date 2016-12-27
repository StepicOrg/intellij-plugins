package com.jetbrains.tmp.learning.navigation;

import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.Step;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StudyNavigator {
    private StudyNavigator() {
    }

    @Nullable
    private static Step navigate(@NotNull final Step step, @NotNull Direction direction) {
        Lesson lesson = step.getLesson();
        if (lesson == null) {
            return null;
        }
        Step item = null;
        switch (direction) {
            case BACK:
                item = lesson.getPrevStep(step);
                break;
            case FORWARD:
                item = lesson.getNextStep(step);
                break;
        }
        if (item != null) {
            return item;
        }

        while ((lesson = navigate(lesson, direction)) != null) {
            switch (direction) {
                case BACK:
                    item = lesson.getLastStep();
                    break;
                case FORWARD:
                    item = lesson.getFirstStep();
                    break;
            }
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Nullable
    private static Lesson navigate(@NotNull final Lesson lesson, @NotNull Direction direction) {
        Section section = lesson.getSection();
        if (section == null) {
            return null;
        }
        Lesson item = null;
        switch (direction) {
            case BACK:
                item = section.getPrevLesson(lesson);
                break;
            case FORWARD:
                item = section.getNextLesson(lesson);
                break;
        }
        if (item != null) {
            return item;
        }

        while ((section = navigate(section, direction)) != null) {
            switch (direction) {
                case BACK:
                    item = section.getLastLesson();
                    break;
                case FORWARD:
                    item = section.getFirstLesson();
                    break;
            }
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Nullable
    private static Section navigate(@NotNull final Section section, @NotNull Direction direction) {
        Course course = section.getCourse();
        if (course == null) {
            return null;
        }
        switch (direction) {
            case BACK:
                return course.getPrevSection(section);
            case FORWARD:
                return course.getNextSection(section);
        }
        return null;
    }

    @Nullable
    public static Step nextStep(@NotNull final Step step) {
        return navigate(step, Direction.FORWARD);
    }

    @Nullable
    public static Step previousStep(@NotNull final Step step) {
        return navigate(step, Direction.BACK);
    }

    private enum Direction {
        BACK, FORWARD
    }
}
