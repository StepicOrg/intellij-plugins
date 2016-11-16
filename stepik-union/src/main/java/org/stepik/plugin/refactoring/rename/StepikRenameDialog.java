package org.stepik.plugin.refactoring.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class StepikRenameDialog extends RenameDialog {

    private static final String MESSAGE = "Don't make any changes to the structure of the project tree, it might become defective.";

    public StepikRenameDialog(
            @NotNull Project project,
            @NotNull PsiElement psiElement,
            @Nullable PsiElement nameSuggestionContext, Editor editor) {
        super(project, psiElement, nameSuggestionContext, editor);
    }

    @NotNull
    @Override
    protected String getLabelText() {
        return MESSAGE;
    }

    @Override
    protected boolean hasPreviewButton() {
        return false;
    }

    @Override
    protected boolean areButtonsValid() {
        return false;
    }
}
