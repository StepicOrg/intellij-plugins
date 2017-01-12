package com.jetbrains.tmp.learning.navigation;

import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.SectionNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StudyNavigator {
    private StudyNavigator() {
    }

    @Nullable
    private static StepNode navigate(@NotNull final StepNode stepNode, @NotNull Direction direction) {
        LessonNode lessonNode = stepNode.getLessonNode();
        if (lessonNode == null) {
            return null;
        }
        StepNode item = null;
        switch (direction) {
            case BACK:
                item = lessonNode.getPrevStep(stepNode);
                break;
            case FORWARD:
                item = lessonNode.getNextStep(stepNode);
                break;
        }
        if (item != null) {
            return item;
        }

        while ((lessonNode = navigate(lessonNode, direction)) != null) {
            switch (direction) {
                case BACK:
                    item = lessonNode.getLastStep();
                    break;
                case FORWARD:
                    item = lessonNode.getFirstStep();
                    break;
            }
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Nullable
    private static LessonNode navigate(@NotNull final LessonNode lessonNode, @NotNull Direction direction) {
        SectionNode sectionNode = lessonNode.getSectionNode();
        if (sectionNode == null) {
            return null;
        }
        LessonNode item = null;
        switch (direction) {
            case BACK:
                item = sectionNode.getPrevLesson(lessonNode);
                break;
            case FORWARD:
                item = sectionNode.getNextLesson(lessonNode);
                break;
        }
        if (item != null) {
            return item;
        }

        while ((sectionNode = navigate(sectionNode, direction)) != null) {
            switch (direction) {
                case BACK:
                    item = sectionNode.getLastLesson();
                    break;
                case FORWARD:
                    item = sectionNode.getFirstLesson();
                    break;
            }
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Nullable
    private static SectionNode navigate(@NotNull final SectionNode sectionNode, @NotNull Direction direction) {
        CourseNode courseNode = sectionNode.getCourseNode();
        if (courseNode == null) {
            return null;
        }
        switch (direction) {
            case BACK:
                return courseNode.getPrevSection(sectionNode);
            case FORWARD:
                return courseNode.getNextSection(sectionNode);
        }
        return null;
    }

    @Nullable
    public static StepNode nextStep(@NotNull final StepNode stepNode) {
        return navigate(stepNode, Direction.FORWARD);
    }

    @Nullable
    public static StepNode previousStep(@NotNull final StepNode stepNode) {
        return navigate(stepNode, Direction.BACK);
    }

    private enum Direction {
        BACK, FORWARD
    }
}
