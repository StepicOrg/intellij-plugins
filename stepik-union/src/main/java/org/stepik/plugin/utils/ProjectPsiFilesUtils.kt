package org.stepik.plugin.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import org.stepik.core.StepikProjectManager
import org.stepik.core.utils.ProjectFilesUtils


object ProjectPsiFilesUtils {

    fun getFile(target: PsiElement): PsiFileSystemItem? {
        return (target as? PsiFileSystemItem) ?: target.containingFile
    }

    fun isCanNotBeTarget(
            target: PsiElement?,
            acceptableClasses: Set<Class<out PsiElement>>): Boolean {
        if (target == null) {
            return false
        }

        val project = target.project
        if (!StepikProjectManager.isStepikProject(project)) {
            return false
        }

        if (notAccept(target, acceptableClasses)) {
            return false
        }

        val item = getFile(target) ?: return false

        val targetPath = getRelativePath(item)

        return ProjectFilesUtils.isCanNotBeTarget(targetPath)
    }

    private fun notAccept(
            target: PsiElement,
            acceptableClasses: Set<Class<out PsiElement>>): Boolean {
        for (clazz in acceptableClasses) {
            if (clazz.isInstance(target)) {
                return false
            }
        }
        return true
    }

    fun getRelativePath(item: PsiFileSystemItem): String {
        val path = item.virtualFile.path
        val projectPath = item.project.basePath ?: return path
        return ProjectFilesUtils.getRelativePath(projectPath, path)
    }

    fun isNotMovableOrRenameElement(
            element: PsiElement,
            acceptableClasses: Set<Class<out PsiElement>>): Boolean {
        val project = element.project

        val root = StepikProjectManager.getProjectRoot(project) ?: return false

        if (notAccept(element, acceptableClasses)) {
            return false
        }

        val file = getFile(element) ?: return false
        val path = getRelativePath(file)
        return ProjectFilesUtils.isNotMovableOrRenameElement(root, path)
    }

}
