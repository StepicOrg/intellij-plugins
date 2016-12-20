package org.stepik.core.utils;

import com.intellij.ui.JBColor;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.courseFormat.Task;
import icons.InteractiveLearningIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

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

    private static HashMap<Object, HashMap<StudyStatus, Icon>> iconMap = null;
    private static final JBColor SOLVED_COLOR = new JBColor(new Color(0, 134, 0), new Color(98, 150, 85));

    @Nullable
    public static HashMap<StudyStatus, Icon> getIconMap(@NotNull Class subjectClass) {
        if (iconMap == null) {
            iconMap = new HashMap<>();
            HashMap<StudyStatus, Icon> map = new HashMap<>();
            map.put(StudyStatus.UNCHECKED, InteractiveLearningIcons.Course);
            map.put(StudyStatus.SOLVED, InteractiveLearningIcons.CourseCompl);
            map.put(StudyStatus.FAILED, InteractiveLearningIcons.Course);
            iconMap.put(Course.class, map);

            map = new HashMap<>();
            map.put(StudyStatus.UNCHECKED, InteractiveLearningIcons.Section);
            map.put(StudyStatus.SOLVED, InteractiveLearningIcons.SectionCompl);
            map.put(StudyStatus.FAILED, InteractiveLearningIcons.Section);
            iconMap.put(Section.class, map);

            map = new HashMap<>();
            map.put(StudyStatus.UNCHECKED, InteractiveLearningIcons.Lesson);
            map.put(StudyStatus.SOLVED, InteractiveLearningIcons.LessonCompl);
            map.put(StudyStatus.FAILED, InteractiveLearningIcons.Lesson);
            iconMap.put(Lesson.class, map);

            map = new HashMap<>();
            map.put(StudyStatus.UNCHECKED, InteractiveLearningIcons.Task);
            map.put(StudyStatus.SOLVED, InteractiveLearningIcons.TaskCompl);
            map.put(StudyStatus.FAILED, InteractiveLearningIcons.TaskProbl);
            iconMap.put(Task.class, map);
        }
        return iconMap.get(subjectClass);
    }

    @NotNull
    public static JBColor getColor(@NotNull StudyStatus status) {
        switch (status) {
            case UNCHECKED:
                return JBColor.BLACK;
            case SOLVED:
                return SOLVED_COLOR;
            case FAILED:
                return JBColor.RED;
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
