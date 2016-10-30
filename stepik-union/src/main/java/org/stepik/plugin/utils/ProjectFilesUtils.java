package org.stepik.plugin.utils;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
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
        String path = PresentationUtils.getRelativePath(element);
        String elementName = element.getName();
        if (path.matches(PresentationUtils.SRC_EXPR + "/\\Q" + elementName + "\\E")) {
            String[] dirs = path.split("/");
            Lesson lesson = course.getLessonOfMnemonic(dirs[1]);
            Task task = lesson.getTask(dirs[2]);
            for (String filename : task.getTaskFiles().keySet()) {
                if (elementName.equals(filename)) {
                    return true;
                }
            }
        }

        return PresentationUtils.isCourseElement(path) || PresentationUtils.isSandbox(path) || ".".equals(path);
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
        String path = PresentationUtils.getRelativePath((PsiFileSystemItem) target);
        if (PresentationUtils.isCourseElement(path) && !path.matches(PresentationUtils.SRC_EXPR) || ".".equals(path)) {
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
}
