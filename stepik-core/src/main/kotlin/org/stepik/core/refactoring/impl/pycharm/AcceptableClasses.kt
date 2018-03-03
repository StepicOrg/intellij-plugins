package org.stepik.core.refactoring.impl.pycharm

import com.intellij.psi.PsiFileSystemItem


object AcceptableClasses {
    fun get() = setOf(
            PsiFileSystemItem::class.java
    )
}
