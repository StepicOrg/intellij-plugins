package org.stepik.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.utils.ProjectFilesUtils;

import java.util.Set;

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

    public static boolean isCanNotBeTarget(
            @Nullable PsiElement target,
            @NotNull Set<Class<? extends PsiElement>> acceptableClasses) {
        if (target == null) {
            return false;
        }

        Project project = target.getProject();
        if (!StepikProjectManager.isStepikProject(project)) {
            return false;
        }

        if (notAccept(target, acceptableClasses)) {
            return false;
        }

        PsiFileSystemItem item = getFile(target);

        if (item == null) {
            return false;
        }

        String targetPath = getRelativePath(item);

        return ProjectFilesUtils.isCanNotBeTarget(targetPath);
    }

    private static boolean notAccept(
            @NotNull PsiElement target,
            @NotNull Set<Class<? extends PsiElement>> acceptableClasses) {
        for (Class<? extends PsiElement> clazz : acceptableClasses) {
            if (clazz.isInstance(target)) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public static String getRelativePath(@NotNull PsiFileSystemItem item) {
        String path = item.getVirtualFile().getPath();
        String projectPath = item.getProject().getBasePath();
        if (projectPath == null) {
            return path;
        }
        return ProjectFilesUtils.getRelativePath(projectPath, path);
    }

    public static boolean isNotMovableOrRenameElement(
            @NotNull PsiElement element,
            @NotNull Set<Class<? extends PsiElement>> acceptableClasses) {
        Project project = element.getProject();
        StepikProjectManager projectManager = StepikProjectManager.getInstance(project);
        if (projectManager == null) {
            return false;
        }
        StudyNode root = projectManager.getProjectRoot();
        if (root == null) {
            return false;
        }

        if (notAccept(element, acceptableClasses)) {
            return false;
        }

        PsiFileSystemItem file = getFile(element);
        if (file == null) {
            return false;
        }
        String path = getRelativePath(file);
        return ProjectFilesUtils.isNotMovableOrRenameElement(root, path);
    }

}