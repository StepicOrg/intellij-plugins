package org.stepik.plugin.utils;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
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

    private static HashMap<StudyStatus, Icon> getIconMap(Object subject) {
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

    private static JBColor getColor(StudyStatus status) {
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

    private static void setAttributes(PresentationData data, String text, JBColor color, Icon icon) {
        data.clearText();
        data.addText(text, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, color));
        data.setIcon(icon);
        data.setPresentableText(text);
    }

    public static boolean isVisibleDirectory(@NotNull PsiDirectory psiDirectory) {
        String name = psiDirectory.getName();

        return !(name.contains(EduNames.USER_TESTS) || name.startsWith(".") || "lib".equals(name) || "hide".equals(name));
    }

    public static boolean isVisibleFile(@NotNull PsiFile psiFile) {
        String name = psiFile.getName();
        PsiDirectory parent = psiFile.getParent();
        if (parent == null)
            return false;
        final String parentName = parent.getName();
        boolean showFiles = EduNames.SRC.equals(parentName) || EduNames.SANDBOX_DIR.equals(parentName);
        return !name.endsWith(".iml") && !name.equals(EduNames.TASK_HTML) && showFiles;
    }

    public static boolean isSourceFile(@NotNull PsiFile psiFile) {
        String name = psiFile.getName();

        return name.endsWith(".java") || name.endsWith(".py");
    }
}
