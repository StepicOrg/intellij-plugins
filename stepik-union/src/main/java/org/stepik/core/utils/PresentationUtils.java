package org.stepik.core.utils;

import com.intellij.ui.JBColor;
import org.stepik.core.courseFormat.CourseNode;
import org.stepik.core.courseFormat.LessonNode;
import org.stepik.core.courseFormat.SectionNode;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.courseFormat.StudyStatus;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static org.stepik.core.utils.ProjectFilesUtils.getParent;
import static org.stepik.core.utils.ProjectFilesUtils.isHideDir;
import static org.stepik.core.utils.ProjectFilesUtils.isSandbox;
import static org.stepik.core.utils.ProjectFilesUtils.isStudyItemDir;
import static org.stepik.core.utils.ProjectFilesUtils.isWithinHideDir;
import static org.stepik.core.utils.ProjectFilesUtils.isWithinSandbox;
import static org.stepik.core.utils.ProjectFilesUtils.isWithinSrc;

/**
 * @author meanmail
 */
public class PresentationUtils {

    private static final JBColor SOLVED_COLOR = new JBColor(new Color(0, 134, 0), new Color(98, 150, 85));
    private static final JBColor WRONG_COLOR = new JBColor(new Color(175, 65, 45), new Color(175, 75, 60));
    private static Icon[][] icons = null;

    @Nullable
    public static Icon getIcon(@NotNull StudyNode studyNode, StudyStatus status) {
        if (icons == null) {
            icons = getIcons();
        }

        Icon[] set;
        Class<? extends StudyNode> clazz = studyNode.getClass();

        if (clazz == StepNode.class) {
            switch (((StepNode) studyNode).getType()) {
                case CODE:
                    set = icons[3];
                    break;
                case TEXT:
                    set = icons[4];
                    break;
                case VIDEO:
                    set = icons[5];
                    break;
                default:
                    set = icons[6];
            }
        } else if (clazz == LessonNode.class) {
            set = icons[2];
        } else if (clazz == SectionNode.class) {
            set = icons[1];
        } else if (clazz == CourseNode.class) {
            set = icons[0];
        } else
            return null;

        switch (status) {
            case SOLVED:
                return set[1];
            default:
                return set[0];
        }
    }

    @NotNull
    private static Icon[][] getIcons() {
        return new Icon[][]{
                {
                        AllStepikIcons.ProjectTree.course,
                        AllStepikIcons.ProjectTree.courseCorrect
                },
                {
                        AllStepikIcons.ProjectTree.module,
                        AllStepikIcons.ProjectTree.moduleCorrect
                },
                {
                        AllStepikIcons.ProjectTree.lesson,
                        AllStepikIcons.ProjectTree.lessonCorrect
                },
                {
                        AllStepikIcons.ProjectTree.stepCode,
                        AllStepikIcons.ProjectTree.stepCodeCorrect
                },
                {
                        AllStepikIcons.ProjectTree.stepText,
                        AllStepikIcons.ProjectTree.stepTextCorrect
                },
                {
                        AllStepikIcons.ProjectTree.stepVideo,
                        AllStepikIcons.ProjectTree.stepVideoCorrect
                },
                {
                        AllStepikIcons.ProjectTree.stepProblem,
                        AllStepikIcons.ProjectTree.stepProblemCorrect
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
        if (relPath.startsWith("../")) {
            return true;
        }

        if (isHideDir(relPath) || isWithinHideDir(relPath)) {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if (isSandbox(relPath) || isStudyItemDir(relPath)) {
            return true;
        }

        return isWithinSrc(relPath) || isWithinSandbox(relPath);
    }

    public static boolean isVisibleFile(@NotNull String relFilePath) {
        if (relFilePath.startsWith("../")) {
            return true;
        }

        String parentDir = getParent(relFilePath);
        //noinspection SimplifiableIfStatement
        if (parentDir == null || !isVisibleDirectory(parentDir)) {
            return false;
        }

        return isWithinSrc(relFilePath) || isWithinSandbox(relFilePath);
    }
}
