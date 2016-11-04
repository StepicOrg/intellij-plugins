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

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author meanmail
 */
public class ProjectFilesUtils {

    private static final String SEPARATOR_EXPR = "\\" + File.separator;
    private static final String SECTION_EXPR = EduNames.SECTION + "[0-9]+";
    private static final String LESSON_PATH_EXPR = SECTION_EXPR + SEPARATOR_EXPR + EduNames.LESSON + "[0-9]+";
    private static final String TASK_PATH_EXPR = LESSON_PATH_EXPR + SEPARATOR_EXPR + EduNames.TASK + "[0-9]+";
    private static final String SRC_PATH_EXPR = TASK_PATH_EXPR + SEPARATOR_EXPR + EduNames.SRC;
    private static final String COURSE_DIRECTORIES = "\\.|" + SECTION_EXPR + "|" + LESSON_PATH_EXPR + "|" + TASK_PATH_EXPR + "|" + SRC_PATH_EXPR;
    private static final String HIDE_PATH_EXPR = SRC_PATH_EXPR + SEPARATOR_EXPR + EduNames.HIDE;

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

    private static boolean isNotMovableOrRenameElement(@NotNull Course course, @NotNull PsiFileSystemItem element) {
        String path = getRelativePath(element);
        if (!isWithinSandbox(path) && !isWithinSrc(path)) {
            return true;
        }
        if (isWithinSrc(path)) {
            if (isHideDir(path) || isWithinHideDir(path) || isTaskHtmlFile(path)) {
                return true;
            }
            String[] dirs = splitPath(path);
            Lesson lesson = course.getLessonByDirName(dirs[1]);
            if (lesson == null) {
                return true;
            }
            Task task = lesson.getTask(dirs[2]);
            if (task == null) {
                return true;
            }
            String elementName = element.getName();
            Set<String> filenames = task.getTaskFiles().keySet();
            if (filenames.stream().anyMatch(elementName::equals)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidTarget(final PsiElement target, final PsiElement[] sources) {
        final Course course;
        if (sources.length > 0) {
            Project project = sources[0].getProject();
            course = StudyTaskManager.getInstance(project).getCourse();
        } else {
            return false;
        }
        if (course == null || !EduNames.STEPIK_CODE.equals(course.getCourseMode())) {
            return false;
        }
        if (!(target instanceof PsiFileSystemItem)) {
            return false;
        }

        String targetPath = getRelativePath((PsiFileSystemItem) target);
        if (isHideDir(targetPath) || isWithinHideDir(targetPath)) {
            return false;
        }
        if (!(isWithinSrc(targetPath) || isWithinSandbox(targetPath) || isSrc(targetPath) || isSandbox(targetPath))) {
            return false;
        }

        Stream<PsiElement> stream = Arrays.stream(sources);

        if (stream.anyMatch(source -> !(source instanceof PsiFileSystemItem))) {
            return false;
        }

        return stream.anyMatch(source -> isNotMovableOrRenameElement(course, (PsiFileSystemItem) source));
    }

    public static boolean isTaskHtmlFile(String path) {
        return path.matches(SRC_PATH_EXPR + SEPARATOR_EXPR + EduNames.TASK_HTML);
    }

    public static boolean isSandbox(String path) {
        return path.matches(EduNames.SANDBOX_DIR);
    }

    private static boolean isSrc(String path) {
        return path.matches(SRC_PATH_EXPR);
    }

    public static boolean isWithinSandbox(String path) {
        return path.matches(EduNames.SANDBOX_DIR + SEPARATOR_EXPR + ".*");
    }

    public static boolean isWithinUtil(String path) {
        return path.matches(EduNames.UTIL + SEPARATOR_EXPR + ".*");
    }

    public static boolean isWithinSrc(@NotNull String path) {
        return path.matches(SRC_PATH_EXPR + SEPARATOR_EXPR + ".*");
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
        String relPath = FileUtil.getRelativePath(basePath, path, File.separatorChar);
        return relPath == null? path : relPath;
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
        return path.split(SEPARATOR_EXPR);
    }

    public static boolean isWithinHideDir(@NotNull String path) {
        return path.matches(ProjectFilesUtils.HIDE_PATH_EXPR + SEPARATOR_EXPR + ".*");
    }

    public static boolean isHideDir(String path) {
        return path.matches(ProjectFilesUtils.HIDE_PATH_EXPR);
    }

    @Nullable
    public static String getParent(@NotNull String path) {
        File parent = FileUtil.getParentFile(new File(path));
        if (parent == null) {
            return null;
        }
        return parent.getPath();
    }
}
