package org.stepik.plugin.refactoring.move;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ex.MessagesEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.refactoring.move.MoveCallback;
import com.intellij.refactoring.move.MoveHandlerDelegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.plugin.utils.ProjectPsiFilesUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.stepik.plugin.utils.ProjectPsiFilesUtils.isCanNotBeTarget;
import static org.stepik.plugin.utils.ProjectPsiFilesUtils.isNotMovableOrRenameElement;

/**
 * @author meanmail
 */
public abstract class AbstractMoveHandlerDelegate extends MoveHandlerDelegate {
    private final Set<Class<? extends PsiElement>> acceptableClasses = new HashSet<>();

    protected void addAcceptableClasses(@NotNull Set<Class<? extends PsiElement>> classes) {
        acceptableClasses.addAll(classes);
    }

    @Override
    public boolean isMoveRedundant(PsiElement source, PsiElement target) {
        return isNotMovableOrRenameElement(source, acceptableClasses) || isCanNotBeTarget(target, acceptableClasses);
    }

    @Override
    public boolean canMove(PsiElement[] elements, @Nullable PsiElement targetContainer) {
        return isValidTarget(targetContainer, elements);
    }

    @Override
    public boolean isValidTarget(@Nullable PsiElement target, PsiElement[] sources) {
        for (PsiElement source : sources) {
            if (isNotMovableOrRenameElement(source, acceptableClasses)) {
                return true;
            }
        }

        return isCanNotBeTarget(target, acceptableClasses);
    }

    @Override
    public void doMove(
            Project project,
            PsiElement[] elements,
            @Nullable PsiElement targetContainer,
            @Nullable MoveCallback callback) {
        List<PsiFileSystemItem> sources = Arrays.stream(elements)
                .filter(psiElement -> isNotMovableOrRenameElement(psiElement, acceptableClasses))
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
