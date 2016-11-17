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

    private static final HashMap<Object, HashMap<StudyStatus, Icon>> iconMap = new HashMap<>();

    @Nullable
    public static HashMap<StudyStatus, Icon> getIconMap(@Nullable Object subject) {
        HashMap<StudyStatus, Icon> result = iconMap.get(subject);
        if (result != null)
            return result;

        if (subject instanceof Course) {
            HashMap<StudyStatus, Icon> map = new HashMap<>();
            map.put(StudyStatus.Unchecked, InteractiveLearningIcons.Course);
            map.put(StudyStatus.Solved, InteractiveLearningIcons.CourseCompl);
            map.put(StudyStatus.Failed, InteractiveLearningIcons.Course);
            iconMap.put(subject, map);
            return map;
        }
        if (subject instanceof Section) {
            HashMap<StudyStatus, Icon> map = new HashMap<>();
            map.put(StudyStatus.Unchecked, InteractiveLearningIcons.Section);
            map.put(StudyStatus.Solved, InteractiveLearningIcons.SectionCompl);
            map.put(StudyStatus.Failed, InteractiveLearningIcons.Section);
            iconMap.put(subject, map);
            return map;
        }
        if (subject instanceof Lesson) {
            HashMap<StudyStatus, Icon> map = new HashMap<>();
            map.put(StudyStatus.Unchecked, InteractiveLearningIcons.Lesson);
            map.put(StudyStatus.Solved, InteractiveLearningIcons.LessonCompl);
            map.put(StudyStatus.Failed, InteractiveLearningIcons.Lesson);
            iconMap.put(subject, map);
            return map;
        }
        if (subject instanceof Task) {
            HashMap<StudyStatus, Icon> map = new HashMap<>();
            map.put(StudyStatus.Unchecked, InteractiveLearningIcons.Task);
            map.put(StudyStatus.Solved, InteractiveLearningIcons.TaskCompl);
            map.put(StudyStatus.Failed, InteractiveLearningIcons.TaskProbl);
            iconMap.put(subject, map);
            return map;
        }
        return null;
    }

    @NotNull
    public static JBColor getColor(@NotNull StudyStatus status) {
        switch (status) {
            case Unchecked:
                return JBColor.BLACK;
            case Solved:
                return new JBColor(new Color(0, 134, 0), new Color(98, 150, 85));
            case Failed:
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
