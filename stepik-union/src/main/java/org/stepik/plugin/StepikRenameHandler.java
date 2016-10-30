package org.stepik.plugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameHandler;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.utils.ProjectFilesUtils;

/**
 * @author meanmail
 */
public class StepikRenameHandler implements RenameHandler {
    @Override
    public boolean isAvailableOnDataContext(DataContext dataContext) {
        return ProjectFilesUtils.canMoveOrRename(dataContext);
    }

    @Override
    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        // Ignored renaming
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        invoke(project, null, null, dataContext);
    }

    @Override
    public String toString() {
        return "Don't rename it! The course can be defective";
    }
}
