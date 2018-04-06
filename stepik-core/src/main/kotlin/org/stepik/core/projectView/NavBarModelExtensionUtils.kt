package org.stepik.core.projectView

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.stepik.core.getProjectRoot
import org.stepik.core.isStepikProject
import org.stepik.core.utils.isVisible
import org.stepik.core.utils.updatePresentationData

object NavBarModelExtensionUtils {
    fun getPresentableText(any: Any?): String? {
        if (any is Project) {
            return getProjectRoot(any)?.name
        }
        
        if (any is PsiDirectory && isStepikProject(any.project)) {
            val data = PresentationData()
            updatePresentationData(data, any)
            return data.presentableText
        }
        
        return null
    }
    
    fun adjustElement(psiElement: PsiElement): PsiElement? {
        if (!isStepikProject(psiElement.project)) {
            return psiElement
        }
        
        when (psiElement) {
            is PsiDirectory -> if (!psiElement.isVisible()) return null
            is PsiFile      -> if (!psiElement.isVisible()) return null
        }
        
        return psiElement
    }
}
