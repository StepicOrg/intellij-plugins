package org.stepik.core.refactoring.impl.idea

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFileSystemItem


object AcceptableClasses {
    fun get() = setOf(
            PsiFileSystemItem::class.java,
            PsiClass::class.java
    )
}
