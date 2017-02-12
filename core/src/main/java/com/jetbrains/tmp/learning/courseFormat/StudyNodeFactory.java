package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.StudyObject;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
import org.stepik.api.objects.lessons.Lesson;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.steps.Step;

/**
 * @author meanmail
 */
public class StudyNodeFactory {
    public static StudyNode createTree(@NotNull StudyObject data, ProgressIndicator indicator) {
        StudyNode root = null;
        if (data instanceof Course) {
            root = new CourseNode((Course) data, indicator);
        } else if (data instanceof Section) {
            root = new SectionNode((Section) data, indicator);
        } else if (data instanceof Lesson) {
            CompoundUnitLesson compoundData = new CompoundUnitLesson(null, (Lesson) data);
            root = new LessonNode(compoundData, indicator);
        } else if (data instanceof CompoundUnitLesson) {
            root = new LessonNode((CompoundUnitLesson) data, indicator);
        } else if (data instanceof Step) {
            root = new StepNode((Step) data, indicator);
        }
        return root;
    }
}
