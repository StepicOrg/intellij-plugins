package org.stepik.plugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.refactoring.move.MoveCallback;
import com.intellij.refactoring.move.MoveHandlerDelegate;
import com.jetbrains.tmp.learning.StudyUtils;
import org.jetbrains.annotations.Nullable;
import org.stepik.plugin.utils.ProjectFilesUtils;

/**
 * @author meanmail
 */
public class StepikMoveHandlerDelegate extends MoveHandlerDelegate {
    @Override
    public boolean canMove(DataContext dataContext) {
        return ProjectFilesUtils.canMoveOrRename(dataContext);
    }

    @Override
    public boolean isValidTarget(PsiElement target, PsiElement[] sources) {
        return ProjectFilesUtils.isValidTarget(target, sources);
    }

    @Override
    public void doMove(
            final Project project,
            PsiElement[] elements,
            @Nullable PsiElement targetContainer,
            @Nullable MoveCallback callback) {
        // Ignore moving for NotMovable elements
    }

    @Override
    public boolean tryToMove(
            PsiElement element,
            Project project,
            DataContext dataContext,
            @Nullable PsiReference reference,
            Editor editor) {
        return StudyUtils.isStudyProject(project);
    }
}
