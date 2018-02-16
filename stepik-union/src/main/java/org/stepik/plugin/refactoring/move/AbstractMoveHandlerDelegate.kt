package org.stepik.plugin.refactoring.move

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ex.MessagesEx
import com.intellij.psi.PsiElement
import com.intellij.refactoring.move.MoveCallback
import com.intellij.refactoring.move.MoveHandlerDelegate
import org.stepik.plugin.utils.ProjectPsiFilesUtils
import org.stepik.plugin.utils.ProjectPsiFilesUtils.isCanNotBeTarget
import org.stepik.plugin.utils.ProjectPsiFilesUtils.isNotMovableOrRenameElement
import java.util.*


abstract class AbstractMoveHandlerDelegate : MoveHandlerDelegate() {
    private val acceptableClasses = HashSet<Class<out PsiElement>>()

    protected fun addAcceptableClasses(classes: Set<Class<out PsiElement>>) {
        acceptableClasses.addAll(classes)
    }

    override fun isMoveRedundant(source: PsiElement?, target: PsiElement?): Boolean {
        if (source == null) {
            return true
        }
        return isNotMovableOrRenameElement(source, acceptableClasses) || isCanNotBeTarget(target, acceptableClasses)
    }

    override fun canMove(elements: Array<PsiElement>, targetContainer: PsiElement?): Boolean {
        return isValidTarget(targetContainer, elements)
    }

    override fun isValidTarget(target: PsiElement?, sources: Array<PsiElement>?): Boolean {
        if (sources == null) {
            return false
        }

        return sources.any { isNotMovableOrRenameElement(it, acceptableClasses) } || isCanNotBeTarget(target, acceptableClasses)
    }

    override fun doMove(
            project: Project?,
            elements: Array<PsiElement>?,
            targetContainer: PsiElement?,
            callback: MoveCallback?) {
        val sources = elements!!.filter { isNotMovableOrRenameElement(it, acceptableClasses) }
                .map { ProjectPsiFilesUtils.getFile(it) }
                .filter { Objects.nonNull(it) }

        val message = StringBuilder()
        if (sources.count() > 1) {
            message.append("You can not move the following elements:")
        } else {
            val source = sources.firstOrNull()
            message.append("You can not move the ")
                    .append(if (source?.isDirectory == true) "directory" else "file")
                    .append(":")
        }

        for (file in sources) {
            message.append("\n")
                    .append(file?.virtualFile?.path)
        }

        MessagesEx.error(project, message.toString(), "Move").showNow()
    }
}
