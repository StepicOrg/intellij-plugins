package org.stepik.core.projectView

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.stepik.core.StepikProjectManager
import org.stepik.plugin.utils.PresentationDataUtils.isVisibleDirectory
import org.stepik.plugin.utils.PresentationDataUtils.isVisibleFile
import org.stepik.plugin.utils.PresentationDataUtils.updatePresentationData


object NavBarModelExtensionUtils {
    fun getPresentableText(any: Any?): String? {
        if (any is Project) {
            val project = any as Project?

            val root = StepikProjectManager.getProjectRoot(project!!) ?: return null
            return root.name
        }

        if (any is PsiDirectory) {
            val psiDirectory = any as PsiDirectory?
            val data = PresentationData()
            updatePresentationData(data, psiDirectory!!)
            val text = data.presentableText
            if (text != null)
                return text
        }

        return null
    }

    fun adjustElement(psiElement: PsiElement): PsiElement? {
        val project = psiElement.project

        StepikProjectManager.getProjectRoot(project) ?: return psiElement

        if (psiElement is PsiDirectory) {
            if (!isVisibleDirectory(psiElement))
                return null
        } else if (psiElement is PsiFile) {
            if (!isVisibleFile(psiElement))
                return null
        }

        return psiElement
    }
}
