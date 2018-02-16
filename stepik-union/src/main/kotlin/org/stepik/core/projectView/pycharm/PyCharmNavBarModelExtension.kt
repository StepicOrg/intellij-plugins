package org.stepik.core.projectView.pycharm

import com.intellij.ide.navigationToolbar.DefaultNavBarExtension
import com.intellij.psi.PsiElement
import com.intellij.util.Processor
import org.stepik.core.projectView.NavBarModelExtensionUtils


class PyCharmNavBarModelExtension : DefaultNavBarExtension() {
    override fun getPresentableText(any: Any?): String? {
        val text = NavBarModelExtensionUtils.getPresentableText(any)
        return text ?: super.getPresentableText(any)
    }

    override fun adjustElement(psiElement: PsiElement): PsiElement? {
        NavBarModelExtensionUtils.adjustElement(psiElement) ?: return null
        return super.adjustElement(psiElement)
    }

    override fun processChildren(any: Any?, rootElement: Any?, processor: Processor<Any>?) = true
}
