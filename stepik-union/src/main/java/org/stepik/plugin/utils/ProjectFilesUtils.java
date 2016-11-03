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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class ProjectFilesUtils {

    public static final String SECTION_EXPR = EduNames.SECTION + "[0-9]+";
    public static final String LESSON_EXPR = SECTION_EXPR + "/" + EduNames.LESSON + "[0-9]+";
    public static final String TASK_EXPR = LESSON_EXPR + "/" + EduNames.TASK + "[0-9]+";
    public static final String SRC_EXPR = TASK_EXPR + "/" + EduNames.SRC;
    public static final String SOURCE_DIRECTORY = SECTION_EXPR + "|" + LESSON_EXPR + "|" + TASK_EXPR + "|" + SRC_EXPR;

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
        String elementName = element.getName();
        if (path != null && path.matches(SRC_EXPR + "/\\Q" + elementName + "\\E")) {
            String[] dirs = path.split("/");
            Lesson lesson = course.getLessonByDirName(dirs[1]);
            if (lesson == null) {
                return true;
            }
            Task task = lesson.getTask(dirs[2]);
            if (task == null) {
                return true;
            }
            for (String filename : task.getTaskFiles().keySet()) {
                if (elementName.equals(filename)) {
                    return true;
                }
            }
        }

        return path == null || isStudyItemDir(path) || isSandbox(path) || ".".equals(path);
    }

    public static boolean isValidTarget(PsiElement target, PsiElement[] sources) {
        Course course = null;
        if (sources.length > 0) {
            Project project = sources[0].getProject();
            course = StudyTaskManager.getInstance(project).getCourse();
        }
        if (course == null || !EduNames.STEPIK_CODE.equals(course.getCourseMode())) {
            return false;
        }
        if (!(target instanceof PsiFileSystemItem)) {
            return false;
        }
        String path = getRelativePath((PsiFileSystemItem) target);
        if (path != null && (isStudyItemDir(path) && !path.matches(SRC_EXPR) || ".".equals(path))) {
            return true;
        }

        for (PsiElement element : sources) {
            if ((element instanceof PsiFileSystemItem)) {
                if (isNotMovableOrRenameElement(course, (PsiFileSystemItem) element)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public static String getRelativePath(@NotNull PsiFileSystemItem item) {
        String path = item.getVirtualFile().getPath();
        String projectPath = item.getProject().getBasePath();
        if (projectPath == null) {
            return path;
        }
        return FileUtil.getRelativePath(projectPath, path, '/');
    }

    public static boolean isStudyItemDir(@NotNull String relativePath) {
        return relativePath.startsWith(EduNames.UTIL) || relativePath.matches(SOURCE_DIRECTORY);
    }

    public static boolean isSandbox(@Nullable String relativePath) {
        return EduNames.SANDBOX_DIR.equals(relativePath);
    }
}
