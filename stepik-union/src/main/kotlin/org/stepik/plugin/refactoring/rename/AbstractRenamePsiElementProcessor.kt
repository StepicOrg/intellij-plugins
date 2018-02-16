package org.stepik.plugin.refactoring.rename

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenameDialog
import com.intellij.refactoring.rename.RenamePsiElementProcessor
import org.stepik.plugin.utils.ProjectPsiFilesUtils.isNotMovableOrRenameElement
import java.util.*


abstract class AbstractRenamePsiElementProcessor : RenamePsiElementProcessor() {
    private val acceptableClasses = HashSet<Class<out PsiElement>>()

    protected fun addAcceptableClasses(classes: Set<Class<out PsiElement>>) {
        acceptableClasses.addAll(classes)
    }

    override fun canProcessElement(element: PsiElement): Boolean {
        return isNotMovableOrRenameElement(element, acceptableClasses)
    }

    override fun createRenameDialog(
            project: Project, element: PsiElement, nameSuggestionContext: PsiElement, editor: Editor): RenameDialog {
        return StepikRenameDialog(project, element, nameSuggestionContext, editor)
    }
}
