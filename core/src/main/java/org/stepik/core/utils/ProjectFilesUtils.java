package org.stepik.core.utils;

import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class ProjectFilesUtils {

    public static final String SEPARATOR = "/";
    private static final char SEPARATOR_CHAR = '/';
    private static final String SECTION_EXPR = EduNames.SECTION + "[0-9]+";
    private static final String LESSON_PATH_EXPR = SECTION_EXPR + SEPARATOR + EduNames.LESSON + "[0-9]+";
    private static final String TASK_PATH_EXPR = LESSON_PATH_EXPR + SEPARATOR + EduNames.STEP + "[0-9]+";
    private static final String SRC_PATH_EXPR = TASK_PATH_EXPR + SEPARATOR + EduNames.SRC;
    private static final String COURSE_DIRECTORIES = "\\.|" + SECTION_EXPR + "|" + LESSON_PATH_EXPR + "|" + TASK_PATH_EXPR + "|" + SRC_PATH_EXPR;
    private static final String HIDE_PATH_EXPR = SRC_PATH_EXPR + SEPARATOR + EduNames.HIDE;

    public static boolean isCanNotBeTarget(@NotNull String targetPath) {
        //noinspection SimplifiableIfStatement
        if (isHideDir(targetPath) || isWithinHideDir(targetPath)) {
            return true;
        }
        return !(isWithinSrc(targetPath) || isWithinSandbox(targetPath) || isSandbox(targetPath) || isSrc(targetPath));
    }

    private static boolean isStepFile(@NotNull CourseNode courseNode, @NotNull String path) {
        String[] dirs = splitPath(path);
        if (dirs.length > 3) {
            LessonNode lessonNode = courseNode.getLessonByDirName(dirs[1]);
            if (lessonNode == null) {
                return false;
            }
            StepNode stepNode = lessonNode.getStep(dirs[2]);
            if (stepNode == null) {
                return false;
            }
            String fileName = dirs[dirs.length - 1];
            return stepNode.isStepFile(fileName);
        }
        return false;
    }

    public static boolean isNotMovableOrRenameElement(@NotNull CourseNode courseNode, @NotNull String path) {
        if (isWithinSrc(path)) {
            return isHideDir(path) || isWithinHideDir(path) || isStepFile(courseNode, path);
        }

        return !isWithinSandbox(path);
    }

    public static boolean isSandbox(@NotNull String path) {
        return path.matches(EduNames.SANDBOX_DIR);
    }

    private static boolean isSrc(@NotNull String path) {
        return path.matches(SRC_PATH_EXPR);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean isWithinSandbox(@NotNull String path) {
        return path.matches(EduNames.SANDBOX_DIR + SEPARATOR + ".*");
    }

    static boolean isWithinSrc(@NotNull String path) {
        return path.matches(SRC_PATH_EXPR + SEPARATOR + ".*");
    }

    @NotNull
    public static String getRelativePath(@NotNull String basePath, @NotNull String path) {
        String relativePath = FileUtil.getRelativePath(basePath, path, SEPARATOR_CHAR);
        return relativePath == null ? path : relativePath;
    }

    static boolean isStudyItemDir(@NotNull String relativePath) {
        return relativePath.matches(COURSE_DIRECTORIES);
    }

    @NotNull
    private static String[] splitPath(@NotNull String path) {
        return path.split(SEPARATOR);
    }

    static boolean isWithinHideDir(@NotNull String path) {
        return path.matches(HIDE_PATH_EXPR + SEPARATOR + ".*");
    }

    static boolean isHideDir(@NotNull String path) {
        return path.matches(HIDE_PATH_EXPR);
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
