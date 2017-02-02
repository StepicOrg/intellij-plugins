package org.stepik.plugin.refactoring.impl.idea;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author meanmail
 */
public class AcceptableClasses {
    @NotNull
    public static Set<Class<? extends PsiElement>> get() {
        Set<Class<? extends PsiElement>> acceptableClasses = new HashSet<>();

        acceptableClasses.add(PsiFileSystemItem.class);
        acceptableClasses.add(PsiClass.class);

        return acceptableClasses;
    }
}
