package org.stepik.plugin.refactoring.impl.pycharm

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import java.util.*


object AcceptableClasses {
    fun get(): Set<Class<out PsiElement>> {
        val acceptableClasses = HashSet<Class<out PsiElement>>()

        acceptableClasses.add(PsiFileSystemItem::class.java)

        return acceptableClasses
    }
}
