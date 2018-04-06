package org.stepik.core.projectView.idea

import com.intellij.ide.navigationToolbar.JavaNavBarExtension
import com.intellij.psi.PsiElement
import org.stepik.core.projectView.NavBarModelExtensionUtils

class IdeaNavBarModelExtension : JavaNavBarExtension() {
    override fun getPresentableText(any: Any?): String? {
        val text = NavBarModelExtensionUtils.getPresentableText(any)
        return text ?: super.getPresentableText(any)
    }
    
    override fun adjustElement(psiElement: PsiElement): PsiElement? {
        NavBarModelExtensionUtils.adjustElement(psiElement) ?: return null
        return super.adjustElement(psiElement)
    }
}
