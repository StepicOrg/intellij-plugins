package org.stepik.plugin.refactoring.move;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.move.MoveHandlerDelegate;

import static org.stepik.plugin.utils.ProjectFilesUtils.isNotMovableOrRenameElement;
import static org.stepik.plugin.utils.ProjectFilesUtils.isCanNotBeTarget;

/**
 * @author meanmail
 */
public class StepikMoveHandlerDelegate extends MoveHandlerDelegate {
    @Override
    public boolean isMoveRedundant(PsiElement source, PsiElement target) {
        return isNotMovableOrRenameElement(source) || isCanNotBeTarget(target);
    }
}
