package org.stepik.plugin;

import com.intellij.ide.navigationToolbar.NavBarModelExtension;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Пользователь on 21.10.2016.
 */
public class StepikNavBarModelExtention implements NavBarModelExtension {

    @Nullable
    @Override
    public String getPresentableText(Object object) {
        return object.toString();
    }

    @Nullable
    @Override
    public PsiElement getParent(PsiElement psiElement) {
        return psiElement.getParent();
    }

    @Nullable
    @Override
    public PsiElement adjustElement(PsiElement psiElement) {
        return psiElement;
    }

    @NotNull
    @Override
    public Collection<VirtualFile> additionalRoots(Project project) {
        return new ArrayList<>();
    }
}
