package com.jetbrains.tmp.learning.navigation;

import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Task;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StudyNavigator {
    private StudyNavigator() {
    }

    public static Task nextTask(@NotNull final Task task) {
        Lesson currentLesson = task.getLesson();
        List<Task> taskList = currentLesson.getTaskList();
        if (task.getIndex() < taskList.size()) {
            return taskList.get(task.getIndex());
        }
        Lesson nextLesson = nextLesson(currentLesson);
        if (nextLesson == null) {
            return null;
        }
        return StudyUtils.getFirst(nextLesson.getTaskList());
    }

    public static Task previousTask(@NotNull final Task task) {
        Lesson currentLesson = task.getLesson();
        int prevTaskIndex = task.getIndex() - 2;
        if (prevTaskIndex >= 0) {
            return currentLesson.getTaskList().get(prevTaskIndex);
        }
        Lesson prevLesson = previousLesson(currentLesson);
        if (prevLesson == null) {
            return null;
        }
        //getting last task in previous lesson
        return prevLesson.getTaskList().get(prevLesson.getTaskList().size() - 1);
    }

    private static Lesson nextLesson(@NotNull final Lesson lesson) {
        Course course = lesson.getSection().getCourse();
        if (course == null) {
            return null;
        }

        int index = lesson.getIndex();

        Lesson nextLesson = course.getLessonOfIndex(index + 1);

        if (nextLesson == null || EduNames.PYCHARM_ADDITIONAL.equals(nextLesson.getName())) {
            return null;
        }
        return nextLesson;
    }

    private static Lesson previousLesson(@NotNull final Lesson lesson) {
        Course course = lesson.getSection().getCourse();
        if (course == null)
            return null;

        int index = lesson.getIndex();
        if (index <= 0) {
            return null;
        }

        return course.getLessonOfIndex(index - 1);
    }
}
