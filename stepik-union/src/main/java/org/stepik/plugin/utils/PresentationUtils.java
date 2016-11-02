package org.stepik.plugin.utils;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Section;
import com.jetbrains.tmp.learning.courseFormat.StudyItem;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.courseFormat.Task;
import icons.InteractiveLearningIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * @author meanmail
 */
public class PresentationUtils {

    public static void updatePresentationData(@NotNull PresentationData data, @NotNull PsiDirectory psiDirectory) {
        Project project = psiDirectory.getProject();
        String valueName = psiDirectory.getName();
        StudyTaskManager studyTaskManager = StudyTaskManager.getInstance(project);
        Course course = studyTaskManager.getCourse();
        if (course == null) {
            return;
        }
        VirtualFile baseDir = project.getBaseDir();
        String name = baseDir.getName();
        if (valueName.equals(name)) {
            setAttributes(data, course);
        } else if (valueName.startsWith(EduNames.TASK)) {
            PsiDirectory lessonDirectory = psiDirectory.getParent();
            if (lessonDirectory != null) {
                Lesson lesson = course.getLessonOfMnemonic(lessonDirectory.getName());
                if (lesson != null) {
                    Task task = lesson.getTask(psiDirectory.getName());
                    if (task != null) {
                        setAttributes(data, task);
                    }
                }
            }
        } else if (valueName.startsWith(EduNames.LESSON)) {
            PsiDirectory parent = psiDirectory.getParent();
            if (parent == null) {
                return;
            }
            Lesson lesson = course.getLessonOfMnemonic(valueName);
            setAttributes(data, lesson);
        } else if (valueName.startsWith(EduNames.SECTION)) {
            Section section = course.getSectionOfMnemonic(valueName);
            if (section != null) {
                setAttributes(data, section);
            }
        } else if (valueName.contains(EduNames.SANDBOX_DIR)) {
            PsiDirectory parent = psiDirectory.getParent();
            if (parent != null) {
                if (!parent.getName().contains(EduNames.SANDBOX_DIR)) {
                    setAttributes(data, EduNames.SANDBOX_DIR, JBColor.BLACK, InteractiveLearningIcons.Sandbox);
                }
            }
        } else
            data.setPresentableText(valueName);
    }

    private static final HashMap<Object, HashMap<StudyStatus, Icon>> iconMap = new HashMap<>();

    @Nullable
    private static HashMap<StudyStatus, Icon> getIconMap(@Nullable Object subject) {
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
    private static JBColor getColor(@NotNull StudyStatus status) {
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

    private static void setAttributes(@NotNull PresentationData data, @NotNull StudyItem item) {
        String text = item.getName();
        HashMap<StudyStatus, Icon> iconMap = getIconMap(item);
        StudyStatus status = item.getStatus();
        JBColor color = getColor(status);
        Icon icon = iconMap != null ? iconMap.get(status) : null;
        setAttributes(data, text, color, icon);
    }

    private static void setAttributes(@NotNull PresentationData data, String text, JBColor color, Icon icon) {
        data.clearText();
        data.addText(text, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, color));
        data.setIcon(icon);
        data.setPresentableText(text);
    }

    private static final String SECTION_EXPR = EduNames.SECTION + "[0-9]+";
    private static final String LESSON_EXPR = SECTION_EXPR + "/" + EduNames.LESSON + "[0-9]+";
    private static final String TASK_EXPR = LESSON_EXPR + "/" + EduNames.TASK + "[0-9]+";
    private static final String SRC_EXPR = TASK_EXPR + "/" + EduNames.SRC;
    private static final String SOURCE_DIRECTORY = SECTION_EXPR + "|" + LESSON_EXPR + "|" + TASK_EXPR + "|" + SRC_EXPR;

    public static boolean isVisibleDirectory(@NotNull PsiDirectory psiDirectory) {
        String path = getRelativePath(psiDirectory);
        if (".".equals(path))
            return true;

        if (path.startsWith(EduNames.SANDBOX_DIR) || path.startsWith(EduNames.UTIL) || path.matches(SOURCE_DIRECTORY))
            return true;

        if (psiDirectory.getName().equals(EduNames.HIDE))
            return false;

        String[] dirs = path.split("/");
        return dirs.length > 4;
    }

    public static boolean isVisibleFile(@NotNull PsiFile psiFile) {
        String name = psiFile.getName();
        if (name.endsWith(".iml"))
            return false;

        String path = getRelativePath(psiFile);
        if (path.startsWith(EduNames.SANDBOX_DIR))
            return true;

        if (name.equals(EduNames.TASK_HTML))
            return false;

        String[] dirs = path.split("/");
        return dirs.length > 4;
    }

    @NotNull
    private static String getRelativePath(@NotNull PsiFileSystemItem item) {
        String path = item.getVirtualFile().getPath();
        String projectPath = item.getProject().getBasePath();
        if (projectPath == null) {
            return path;
        }
        String relPath = FileUtil.getRelativePath(projectPath, path, '/');
        return relPath != null ? relPath : path;
    }
}
