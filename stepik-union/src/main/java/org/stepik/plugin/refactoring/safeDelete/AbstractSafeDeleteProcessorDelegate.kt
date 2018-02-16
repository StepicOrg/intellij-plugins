package org.stepik.plugin.refactoring.safeDelete

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.refactoring.safeDelete.NonCodeUsageSearchInfo
import com.intellij.refactoring.safeDelete.SafeDeleteProcessorDelegateBase
import com.intellij.usageView.UsageInfo
import com.intellij.util.IncorrectOperationException
import org.stepik.plugin.utils.ProjectPsiFilesUtils.isNotMovableOrRenameElement
import java.util.*


abstract class AbstractSafeDeleteProcessorDelegate : SafeDeleteProcessorDelegateBase() {
    private val acceptableClasses = HashSet<Class<out PsiElement>>()

    protected fun addAcceptableClasses(classes: Set<Class<out PsiElement>>) {
        acceptableClasses.addAll(classes)
    }

    override fun getElementsToSearch(
            element: PsiElement,
            module: Module?,
            allElementsToDelete: Collection<PsiElement>): Collection<PsiElement>? {
        return null
    }

    override fun handlesElement(element: PsiElement): Boolean {
        return isNotMovableOrRenameElement(element, acceptableClasses)
    }

    override fun findUsages(
            element: PsiElement,
            allElementsToDelete: Array<PsiElement>,
            result: List<UsageInfo>): NonCodeUsageSearchInfo? {
        return null
    }

    override fun getAdditionalElementsToDelete(
            element: PsiElement,
            allElementsToDelete: Collection<PsiElement>,
            askUser: Boolean): Collection<PsiElement>? {
        return null
    }

    override fun findConflicts(element: PsiElement, allElementsToDelete: Array<PsiElement>): Collection<String>? {
        return null
    }

    override fun preprocessUsages(project: Project, usages: Array<UsageInfo>): Array<UsageInfo>? {
        return emptyArray()
    }

    @Throws(IncorrectOperationException::class)
    override fun prepareForDeletion(element: PsiElement) {
        throw IncorrectOperationException("The operation is suspended. Course structure might become defective")
    }

    override fun isToSearchInComments(element: PsiElement): Boolean {
        return false
    }

    override fun setToSearchInComments(element: PsiElement, enabled: Boolean) {

    }

    override fun isToSearchForTextOccurrences(element: PsiElement): Boolean {
        return false
    }

    override fun setToSearchForTextOccurrences(element: PsiElement, enabled: Boolean) {

    }
}
