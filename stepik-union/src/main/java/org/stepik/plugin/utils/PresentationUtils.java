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
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

import static org.stepik.plugin.utils.ProjectFilesUtils.getParent;
import static org.stepik.plugin.utils.ProjectFilesUtils.getRelativePath;
import static org.stepik.plugin.utils.ProjectFilesUtils.isHideDir;
import static org.stepik.plugin.utils.ProjectFilesUtils.isSandbox;
import static org.stepik.plugin.utils.ProjectFilesUtils.isStudyItemDir;
import static org.stepik.plugin.utils.ProjectFilesUtils.isTaskHtmlFile;
import static org.stepik.plugin.utils.ProjectFilesUtils.isUtilDir;
import static org.stepik.plugin.utils.ProjectFilesUtils.isWithinHideDir;
import static org.stepik.plugin.utils.ProjectFilesUtils.isWithinSandbox;
import static org.stepik.plugin.utils.ProjectFilesUtils.isWithinSrc;
import static org.stepik.plugin.utils.ProjectFilesUtils.isWithinUtil;

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
                Lesson lesson = course.getLessonByDirName(lessonDirectory.getName());
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
            Lesson lesson = course.getLessonByDirName(valueName);
            setAttributes(data, lesson);
        } else if (valueName.startsWith(EduNames.SECTION)) {
            Section section = course.getSectionByDirName(valueName);
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

    public static boolean isVisibleDirectory(@NotNull PsiDirectory psiDirectory) {
        Project project = psiDirectory.getProject();
        String basePath = project.getBasePath();
        if (basePath == null) {
            return false;
        }
        String path = psiDirectory.getVirtualFile().getPath();
        String relPath = getRelativePath(basePath, path);

        return isVisibleDirectory(relPath);
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

    public static boolean isVisibleFile(@NotNull PsiFile psiFile) {
        String path = getRelativePath(psiFile);
        return isVisibleFile(path);
    }

    public static boolean isVisibleFile(@NotNull String relFilePath) {
        String parentDir = getParent(relFilePath);
        if (parentDir == null || isTaskHtmlFile(relFilePath) || !isVisibleDirectory(parentDir)) {
            return false;
        }

        return isWithinSrc(relFilePath) || isWithinSandbox(relFilePath) || isWithinUtil(relFilePath);
    }
}
