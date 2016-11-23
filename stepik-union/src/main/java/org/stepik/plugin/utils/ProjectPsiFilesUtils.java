package org.stepik.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.stepik.core.utils.ProjectFilesUtils;

/**
 * @author meanmail
 */
public class ProjectPsiFilesUtils {

    @Nullable
    public static PsiFileSystemItem getFile(@NotNull PsiElement target) {
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
            return false;
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

        return ProjectFilesUtils.isCanNotBeTarget(targetPath);
    }

    @NotNull
    static String getRelativePath(@NotNull PsiFileSystemItem item) {
        String path = item.getVirtualFile().getPath();
        String projectPath = item.getProject().getBasePath();
        if (projectPath == null) {
            return path;
        }
        return ProjectFilesUtils.getRelativePath(projectPath, path);
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
        return ProjectFilesUtils.isNotMovableOrRenameElement(course, path);
    }

}