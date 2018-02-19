package org.stepik.core.refactoring.move

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ex.MessagesEx
import com.intellij.psi.PsiElement
import com.intellij.refactoring.move.MoveCallback
import com.intellij.refactoring.move.MoveHandlerDelegate
import org.stepik.core.utils.ProjectPsiFilesUtils
import org.stepik.core.utils.ProjectPsiFilesUtils.isCanNotBeTarget
import org.stepik.core.utils.ProjectPsiFilesUtils.isNotMovableOrRenameElement


abstract class AbstractMoveHandlerDelegate : MoveHandlerDelegate() {
    private val acceptableClasses: MutableSet<Class<out PsiElement>> = mutableSetOf()

    protected fun addAcceptableClasses(classes: Set<Class<out PsiElement>>) {
        acceptableClasses.addAll(classes)
    }

    override fun isMoveRedundant(source: PsiElement?, target: PsiElement?): Boolean {
        source ?: return true
        return isNotMovableOrRenameElement(source, acceptableClasses) || isCanNotBeTarget(target, acceptableClasses)
    }

    override fun canMove(elements: Array<PsiElement>, targetContainer: PsiElement?): Boolean {
        return isValidTarget(targetContainer, elements)
    }

    override fun isValidTarget(target: PsiElement?, sources: Array<PsiElement>?): Boolean {
        sources ?: return false

        return sources.any { isNotMovableOrRenameElement(it, acceptableClasses) }
                || isCanNotBeTarget(target, acceptableClasses)
    }

    override fun doMove(
            project: Project?,
            elements: Array<PsiElement>?,
            targetContainer: PsiElement?,
            callback: MoveCallback?) {
        elements ?: return
        val sources = elements.filter { isNotMovableOrRenameElement(it, acceptableClasses) }
                .mapNotNull { ProjectPsiFilesUtils.getFile(it) }

        val message = mutableListOf<String>()
        if (sources.count() > 1) {
            message.add("You can not move the following elements:")
        } else {
            val source = sources.firstOrNull()
            message.add("You can not move the ${if (source?.isDirectory == true) "directory" else "file"}:")
        }

        sources.mapNotNullTo(message) { it.virtualFile?.path }

        MessagesEx.error(project, message.joinToString { "\n" }, "Move").showNow()
    }
}
