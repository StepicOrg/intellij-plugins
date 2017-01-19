package org.stepik.plugin.refactoring.move;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ex.MessagesEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.refactoring.move.MoveCallback;
import com.intellij.refactoring.move.MoveHandlerDelegate;
import org.jetbrains.annotations.Nullable;
import org.stepik.plugin.utils.ProjectPsiFilesUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.stepik.plugin.utils.ProjectPsiFilesUtils.isCanNotBeTarget;
import static org.stepik.plugin.utils.ProjectPsiFilesUtils.isNotMovableOrRenameElement;

/**
 * @author meanmail
 */
public class StepikMoveHandlerDelegate extends MoveHandlerDelegate {
    @Override
    public boolean isMoveRedundant(PsiElement source, PsiElement target) {
        return isNotMovableOrRenameElement(source) || isCanNotBeTarget(target);
    }

    @Override
    public boolean canMove(PsiElement[] elements, @Nullable PsiElement targetContainer) {
        return isValidTarget(targetContainer, elements);
    }

    @Override
    public boolean isValidTarget(@Nullable PsiElement target, PsiElement[] sources) {
        for (PsiElement source : sources) {
            if (isNotMovableOrRenameElement(source)) {
                return true;
            }
        }

        return isCanNotBeTarget(target);
    }

    @Override
    public void doMove(
            Project project,
            PsiElement[] elements,
            @Nullable PsiElement targetContainer,
            @Nullable MoveCallback callback) {
        List<PsiFileSystemItem> sources = Arrays.stream(elements)
                .filter(ProjectPsiFilesUtils::isNotMovableOrRenameElement)
                .map(ProjectPsiFilesUtils::getFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        StringBuilder message = new StringBuilder();
        if (sources.size() > 1) {
            message.append("You can not move the following elements:");
        } else {
            PsiFileSystemItem source = sources.get(0);
            message.append("You can not move the ")
                    .append(source.isDirectory() ? "directory" : "file")
                    .append(":");
        }

        for (PsiFileSystemItem file : sources) {
            message.append("\n")
                    .append(file.getVirtualFile().getPath());
        }

        MessagesEx.error(project, message.toString(), "Move").showNow();
    }
}
