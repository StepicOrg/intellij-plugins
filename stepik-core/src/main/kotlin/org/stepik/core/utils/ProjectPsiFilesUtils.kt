package org.stepik.core.utils

import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import org.stepik.core.ProjectManager
import org.stepik.core.StudyUtils.isStepikProject


object ProjectPsiFilesUtils {

    fun getFile(target: PsiElement): PsiFileSystemItem? {
        return (target as? PsiFileSystemItem) ?: target.containingFile
    }

    fun isCanNotBeTarget(
            target: PsiElement?,
            acceptableClasses: Set<Class<out PsiElement>>): Boolean {
        target ?: return false

        if (isStepikProject(target.project)) {
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
        return acceptableClasses.none { it.isInstance(target) }
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

        val projectManager = getService(project, ProjectManager::class.java)
        val root = projectManager?.projectRoot ?: return false

        if (notAccept(element, acceptableClasses)) {
            return false
        }

        val file = getFile(element) ?: return false
        val path = getRelativePath(file)
        return ProjectFilesUtils.isNotMovableOrRenameElement(root, path)
    }

}
