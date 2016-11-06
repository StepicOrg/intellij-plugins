package org.stepik.plugin.utils;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

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

    @Contract("null -> false")
    public static boolean canMoveOrRename(@Nullable DataContext dataContext) {
        if (dataContext == null) {
            return false;
        }
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (element == null || project == null || !(element instanceof PsiFileSystemItem)) {
            return false;
        }
        Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null || !EduNames.STEPIK_CODE.equals(course.getCourseMode())) {
            return false;
        }

        return isNotMovableOrRenameElement(course, (PsiFileSystemItem) element);
    }

    public static boolean isValidTarget(@NotNull final PsiElement target, @NotNull final PsiElement[] sources) {
        if (sources.length == 0) {
            return false;
        }
        final Course course;
        Project project = sources[0].getProject();
        course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null || !EduNames.STEPIK_CODE.equals(course.getCourseMode())) {
            return false;
        }

        if (!(target instanceof PsiFileSystemItem)) {
            return false;
        }

        String targetPath = getRelativePath((PsiFileSystemItem) target);

        if (Arrays.stream(sources).anyMatch(source -> !(source instanceof PsiFileSystemItem))) {
            return false;
        }

        String[] sourcesPaths = new String[sources.length];

        for (int i = 0; i < sources.length; i++) {
            PsiElement source = sources[i];
            sourcesPaths[i] = getRelativePath((PsiFileSystemItem) source);
        }

        return isValidTarget(course, targetPath, sourcesPaths);
    }

    public static boolean isValidTarget(
            @NotNull Course course,
            @NotNull final String targetPath,
            @NotNull final String[] sourcesPaths) {
        if (sourcesPaths.length == 0) {
            return true;
        }

        if (isHideDir(targetPath) || isWithinHideDir(targetPath)) {
            return true;
        }
        if (!(isWithinSrc(targetPath) || isWithinSandbox(targetPath) || isSandbox(targetPath) || isSrc(targetPath))) {
            return true;
        }

        Stream<String> sourcesStream = Arrays.stream(sourcesPaths);

        return sourcesStream.anyMatch(source -> isNotMovableOrRenameElement(course, source));
    }

    private static boolean isNotMovableOrRenameElement(@NotNull Course course, @NotNull PsiFileSystemItem element) {
        String path = getRelativePath(element);
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
        if (!isWithinSandbox(path) && !isWithinSrc(path)) {
            return true;
        }
        if (isWithinSrc(path)) {
            if (isHideDir(path) || isWithinHideDir(path) || isTaskHtmlFile(path)) {
                return true;
            }
            if (isTaskFile(course, path)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isTaskHtmlFile(String path) {
        return path.matches(SRC_PATH_EXPR + SEPARATOR + EduNames.TASK_HTML);
    }

    public static boolean isSandbox(String path) {
        return path.matches(EduNames.SANDBOX_DIR);
    }

    private static boolean isSrc(String path) {
        return path.matches(SRC_PATH_EXPR);
    }

    public static boolean isWithinSandbox(String path) {
        return path.matches(EduNames.SANDBOX_DIR + SEPARATOR + ".*");
    }

    public static boolean isWithinUtil(String path) {
        return path.matches(EduNames.UTIL + SEPARATOR + ".*");
    }

    public static boolean isWithinSrc(@NotNull String path) {
        return path.matches(SRC_PATH_EXPR + SEPARATOR + ".*");
    }

    @NotNull
    public static String getRelativePath(@NotNull PsiFileSystemItem item) {
        String path = item.getVirtualFile().getPath();
        String projectPath = item.getProject().getBasePath();
        if (projectPath == null) {
            return path;
        }
        return getRelativePath(projectPath, path);
    }

    @NotNull
    public static String getRelativePath(@NotNull String basePath, @NotNull String path) {
        String relPath = FileUtil.getRelativePath(basePath, path, SEPARATOR_CHAR);
        return relPath == null ? path : relPath;
    }

    public static boolean isStudyItemDir(@NotNull String relativePath) {
        return relativePath.matches(COURSE_DIRECTORIES);
    }

    @Contract(pure = true)
    public static boolean isUtilDir(@NotNull String relativePath) {
        return relativePath.equals(EduNames.UTIL);
    }

    @NotNull
    private static String[] splitPath(@NotNull String path) {
        return path.split(SEPARATOR);
    }

    public static boolean isWithinHideDir(@NotNull String path) {
        return path.matches(ProjectFilesUtils.HIDE_PATH_EXPR + SEPARATOR + ".*");
    }

    public static boolean isHideDir(String path) {
        return path.matches(ProjectFilesUtils.HIDE_PATH_EXPR);
    }

    @Nullable
    public static String getParent(@NotNull String path) {
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
