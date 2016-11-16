package org.stepik.plugin.refactoring.safeDelete;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.safeDelete.NonCodeUsageSearchInfo;
import com.intellij.refactoring.safeDelete.SafeDeleteProcessorDelegateBase;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static org.stepik.plugin.utils.ProjectFilesUtils.isNotMovableOrRenameElement;

/**
 * @author meanmail
 */
public class StepikSafeDeleteProcessorDelegate extends SafeDeleteProcessorDelegateBase {

    @Nullable
    @Override
    public Collection<? extends PsiElement> getElementsToSearch(
            @NotNull PsiElement element,
            @Nullable Module module,
            @NotNull Collection<PsiElement> allElementsToDelete) {
        return null;
    }

    @Override
    public boolean handlesElement(PsiElement element) {
        return isNotMovableOrRenameElement(element);
    }

    @Nullable
    @Override
    public NonCodeUsageSearchInfo findUsages(
            @NotNull PsiElement element,
            @NotNull PsiElement[] allElementsToDelete,
            @NotNull List<UsageInfo> result) {
        return null;
    }

    @Nullable
    @Override
    public Collection<PsiElement> getAdditionalElementsToDelete(
            @NotNull PsiElement element,
            @NotNull Collection<PsiElement> allElementsToDelete,
            boolean askUser) {
        return null;
    }

    @Nullable
    @Override
    public Collection<String> findConflicts(@NotNull PsiElement element, @NotNull PsiElement[] allElementsToDelete) {
        return null;
    }

    @Nullable
    @Override
    public UsageInfo[] preprocessUsages(Project project, UsageInfo[] usages) {
        return new UsageInfo[0];
    }

    @Override
    public void prepareForDeletion(PsiElement element) throws IncorrectOperationException {
        throw new IncorrectOperationException("The operation is suspended. Course structure might become defective");
    }

    @Override
    public boolean isToSearchInComments(PsiElement element) {
        return false;
    }

    @Override
    public void setToSearchInComments(PsiElement element, boolean enabled) {

    }

    @Override
    public boolean isToSearchForTextOccurrences(PsiElement element) {
        return false;
    }

    @Override
    public void setToSearchForTextOccurrences(PsiElement element, boolean enabled) {

    }
}
