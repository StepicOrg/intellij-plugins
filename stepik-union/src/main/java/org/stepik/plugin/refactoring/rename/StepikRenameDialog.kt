package org.stepik.plugin.refactoring.rename

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenameDialog


internal class StepikRenameDialog(
        project: Project,
        psiElement: PsiElement,
        nameSuggestionContext: PsiElement?,
        editor: Editor) : RenameDialog(project, psiElement, nameSuggestionContext, editor) {

    override fun getLabelText(): String {
        return MESSAGE
    }

    override fun hasPreviewButton(): Boolean {
        return false
    }

    override fun areButtonsValid(): Boolean {
        return false
    }

    companion object {

        private val MESSAGE = "Don't make any changes to the structure of the project tree, it might become defective."
    }
}
