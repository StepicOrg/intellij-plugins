package org.stepik.core.utils;

import com.intellij.ui.JBColor;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.courseFormat.Task;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static org.stepik.core.utils.ProjectFilesUtils.getParent;
import static org.stepik.core.utils.ProjectFilesUtils.isHideDir;
import static org.stepik.core.utils.ProjectFilesUtils.isSandbox;
import static org.stepik.core.utils.ProjectFilesUtils.isStudyItemDir;
import static org.stepik.core.utils.ProjectFilesUtils.isTaskHtmlFile;
import static org.stepik.core.utils.ProjectFilesUtils.isUtilDir;
import static org.stepik.core.utils.ProjectFilesUtils.isWithinHideDir;
import static org.stepik.core.utils.ProjectFilesUtils.isWithinSandbox;
import static org.stepik.core.utils.ProjectFilesUtils.isWithinSrc;
import static org.stepik.core.utils.ProjectFilesUtils.isWithinUtil;

/**
 * @author meanmail
 */
public class PresentationUtils {

    private static Icon[][] icons = null;
    private static final JBColor SOLVED_COLOR = new JBColor(new Color(0, 134, 0), new Color(98, 150, 85));
    private static final JBColor WRONG_COLOR = new JBColor(new Color(175, 65, 45), new Color(175, 75, 60));

    @Nullable
    public static Icon getIcon(@NotNull Object subjectClass, StudyStatus status) {
        if (icons == null) {
            icons = getIcons();
        }

        Icon[] set;

        if (subjectClass == Task.class) {
            set = icons[3];
        } else if (subjectClass == Lesson.class) {
            set = icons[2];
        } else if (subjectClass == Section.class) {
            set = icons[1];
        } else if (subjectClass == Course.class) {
            set = icons[0];
        } else
            return null;

        switch (status) {
            case UNCHECKED:
                return set[0];
            case SOLVED:
                return set[1];
            case FAILED:
                return set[2];
        }
        return null;
    }

    @NotNull
    private static Icon[][] getIcons() {
        return new Icon[][]{
                {
                        AllStepikIcons.ProjectTree.course,
                        AllStepikIcons.ProjectTree.courseCorrect,
                        AllStepikIcons.ProjectTree.course
                },
                {
                        AllStepikIcons.ProjectTree.module,
                        AllStepikIcons.ProjectTree.moduleCorrect,
                        AllStepikIcons.ProjectTree.module
                },
                {
                        AllStepikIcons.ProjectTree.lesson,
                        AllStepikIcons.ProjectTree.lessonCorrect,
                        AllStepikIcons.ProjectTree.lesson
                },
                {
                        AllStepikIcons.ProjectTree.step,
                        AllStepikIcons.ProjectTree.stepCorrect,
                        AllStepikIcons.ProjectTree.stepWrong
                }
        };
    }

    @NotNull
    public static JBColor getColor(@NotNull StudyStatus status) {
        switch (status) {
            case UNCHECKED:
                return JBColor.BLACK;
            case SOLVED:
                return SOLVED_COLOR;
            case FAILED:
                return WRONG_COLOR;
        }
        return JBColor.BLACK;
    }

    public static boolean isVisibleDirectory(@NotNull String relPath) {
        if (isHideDir(relPath) || isWithinHideDir(relPath)) {
            return false;
        }

        if (isSandbox(relPath) || isStudyItemDir(relPath) || isUtilDir(relPath)) {
            return true;
        }

        return isWithinSrc(relPath) || isWithinSandbox(relPath) || isWithinUtil(relPath);
    }

    public static boolean isVisibleFile(@NotNull String relFilePath) {
        String parentDir = getParent(relFilePath);
        if (parentDir == null || isTaskHtmlFile(relFilePath) || !isVisibleDirectory(parentDir)) {
            return false;
        }

        return isWithinSrc(relFilePath) || isWithinSandbox(relFilePath) || isWithinUtil(relFilePath);
    }
}
