package org.stepik.plugin.refactoring.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static org.stepik.plugin.utils.ProjectPsiFilesUtils.isNotMovableOrRenameElement;

/**
 * @author meanmail
 */
public abstract class AbstractRenamePsiElementProcessor extends RenamePsiElementProcessor {
    private final Set<Class<? extends PsiElement>> acceptableClasses = new HashSet<>();

    protected void addAcceptableClasses(@NotNull Set<Class<? extends PsiElement>> classes) {
        acceptableClasses.addAll(classes);
    }

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return isNotMovableOrRenameElement(element, acceptableClasses);
    }

    @Override
    public RenameDialog createRenameDialog(
            Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
        return new StepikRenameDialog(project, element, nameSuggestionContext, editor);
    }
}
