package org.stepik.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Task;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author meanmail
 */
public class ProjectFilesUtils {

    public static final String SEPARATOR = "/";
    private static final char SEPARATOR_CHAR = '/';
    private static final String SECTION_EXPR = EduNames.SECTION + "[0-9]+";
    private static final String LESSON_PATH_EXPR = SECTION_EXPR + SEPARATOR + EduNames.LESSON + "[0-9]+";
    private static final String TASK_PATH_EXPR = LESSON_PATH_EXPR + SEPARATOR + EduNames.TASK + "[0-9]+";
    private static final String SRC_PATH_EXPR = TASK_PATH_EXPR + SEPARATOR + EduNames.SRC;
    private static final String COURSE_DIRECTORIES = "\\.|" + SECTION_EXPR + "|" + LESSON_PATH_EXPR + "|" + TASK_PATH_EXPR + "|" + SRC_PATH_EXPR;
    private static final String HIDE_PATH_EXPR = SRC_PATH_EXPR + SEPARATOR + EduNames.HIDE;

    @Nullable
    private static PsiFileSystemItem getFile(@NotNull PsiElement target) {
        PsiFileSystemItem item;
        if (target instanceof PsiFileSystemItem) {
            item = (PsiFileSystemItem) target;
        } else {
            item = target.getContainingFile();
        }
        return item;
    }

    public static boolean isCanNotBeTarget(@Nullable PsiElement target) {
        if (target == null) {
            return true;
        }

        Project project = target.getProject();
        final Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null || !EduNames.STEPIK_CODE.equals(course.getCourseMode())) {
            return false;
        }

        if (!(target instanceof PsiFileSystemItem || target instanceof PsiClass)) {
            return false;
        }

        PsiFileSystemItem item = getFile(target);

        if (item == null) {
            return false;
        }

        String targetPath = getRelativePath(item);

        return isCanNotBeTarget(targetPath);
    }

    static boolean isCanNotBeTarget(String targetPath) {
        if (isHideDir(targetPath) || isWithinHideDir(targetPath)) {
            return true;
        }
        return !(isWithinSrc(targetPath) || isWithinSandbox(targetPath) || isSandbox(targetPath) || isSrc(targetPath));
    }

    public static boolean isNotMovableOrRenameElement(@NotNull PsiElement element) {
        if (!(element instanceof PsiFileSystemItem || element instanceof PsiClass)) {
            return false;
        }
        Project project = element.getProject();
        Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null || !EduNames.STEPIK_CODE.equals(course.getCourseMode())) {
            return false;
        }
        PsiFileSystemItem file = getFile(element);
        if (file == null) {
            return false;
        }
        String path = getRelativePath(file);
        return isNotMovableOrRenameElement(course, path);
    }

    private static boolean isTaskFile(@NotNull Course course, @NotNull String path) {
        String[] dirs = splitPath(path);
        if (dirs.length > 3) {
            Lesson lesson = course.getLessonByDirName(dirs[1]);
            if (lesson == null) {
                return false;
            }
            Task task = lesson.getTask(dirs[2]);
            if (task == null) {
                return false;
            }
            String fileName = dirs[dirs.length - 1];
            Set<String> filenames = task.getTaskFiles().keySet();
            if (filenames.stream().anyMatch(fileName::equals)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNotMovableOrRenameElement(@NotNull Course course, @NotNull String path) {
        if (isWithinSrc(path)) {
            return isHideDir(path) || isWithinHideDir(path) || isTaskHtmlFile(path) || isTaskFile(course, path);
        }

        return !isWithinSandbox(path);
    }

    static boolean isTaskHtmlFile(String path) {
        return path.matches(SRC_PATH_EXPR + SEPARATOR + EduNames.TASK_HTML);
    }

    static boolean isSandbox(String path) {
        return path.matches(EduNames.SANDBOX_DIR);
    }

    private static boolean isSrc(String path) {
        return path.matches(SRC_PATH_EXPR);
    }

    static boolean isWithinSandbox(String path) {
        return path.matches(EduNames.SANDBOX_DIR + SEPARATOR + ".*");
    }

    static boolean isWithinUtil(String path) {
        return path.matches(EduNames.UTIL + SEPARATOR + ".*");
    }

    static boolean isWithinSrc(@NotNull String path) {
        return path.matches(SRC_PATH_EXPR + SEPARATOR + ".*");
    }

    @NotNull
    static String getRelativePath(@NotNull PsiFileSystemItem item) {
        String path = item.getVirtualFile().getPath();
        String projectPath = item.getProject().getBasePath();
        if (projectPath == null) {
            return path;
        }
        return getRelativePath(projectPath, path);
    }

    @NotNull
    static String getRelativePath(@NotNull String basePath, @NotNull String path) {
        String relativePath = FileUtil.getRelativePath(basePath, path, SEPARATOR_CHAR);
        return relativePath == null ? path : relativePath;
    }

    static boolean isStudyItemDir(@NotNull String relativePath) {
        return relativePath.matches(COURSE_DIRECTORIES);
    }

    @Contract(pure = true)
    static boolean isUtilDir(@NotNull String relativePath) {
        return relativePath.equals(EduNames.UTIL);
    }

    @NotNull
    private static String[] splitPath(@NotNull String path) {
        return path.split(SEPARATOR);
    }

    static boolean isWithinHideDir(@NotNull String path) {
        return path.matches(ProjectFilesUtils.HIDE_PATH_EXPR + SEPARATOR + ".*");
    }

    static boolean isHideDir(String path) {
        return path.matches(ProjectFilesUtils.HIDE_PATH_EXPR);
    }

    @Nullable
    static String getParent(@NotNull String path) {
        String[] dirs = splitPath(path);
        if (dirs.length == 0 || path.isEmpty() || path.equals(".")) {
            return null;
        } else if (dirs.length == 1) {
            return ".";
        }

        StringBuilder parentPath = new StringBuilder(dirs[0]);

        for (int i = 1; i < dirs.length - 1; i++) {
            parentPath.append(SEPARATOR).append(dirs[i]);
        }

        return parentPath.toString();
    }
}
