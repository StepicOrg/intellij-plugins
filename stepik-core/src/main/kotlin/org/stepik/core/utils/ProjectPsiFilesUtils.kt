package org.stepik.core.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import org.stepik.core.getProjectManager
import org.stepik.core.isStepikProject

val PsiElement.file: PsiFileSystemItem?
    get() {
        return (this as? PsiFileSystemItem) ?: this.containingFile
    }

fun PsiElement?.isNotTarget(acceptableClasses: Set<Class<out PsiElement>>): Boolean {
    this ?: return false
    
    if (isStepikProject(this.project)) {
        return false
    }
    
    if (this.notAccept(acceptableClasses)) {
        return false
    }
    
    val item = this.file ?: return false
    
    return item.relativePath.isNotTarget()
}

private fun PsiElement.notAccept(acceptableClasses: Set<Class<out PsiElement>>): Boolean {
    return acceptableClasses.none { it.isInstance(this) }
}

val PsiFileSystemItem.relativePath: String
    get() {
        val path = this.virtualFile.path
        val projectPath = this.project.basePath ?: return path
        return projectPath.getRelativePath(path)
    }

fun PsiElement?.isNotMoveOrRenameElement(acceptableClasses: Set<Class<out PsiElement>>): Boolean {
    val root = getProjectManager(this?.project)?.projectRoot ?: return false
    
    if (this?.notAccept(acceptableClasses) != false) {
        return false
    }
    
    val file = this.file ?: return false
    return isNotMovableOrRenameElement(root, file.relativePath)
}
