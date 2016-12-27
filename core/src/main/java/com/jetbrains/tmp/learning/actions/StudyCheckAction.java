package com.jetbrains.tmp.learning.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.jetbrains.tmp.learning.StudyUtils;
import icons.InteractiveLearningIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class StudyCheckAction extends StudyActionWithShortcut {
    private static final String SHORTCUT = "ctrl alt pressed ENTER";

    private final Ref<Boolean> checkInProgress = new Ref<>(false);

    public StudyCheckAction() {
        super("Check Step (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT),
                null)) + ")", "Check current step", InteractiveLearningIcons.CheckStep);
    }

    public abstract void check(@NotNull final Project project);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        FileDocumentManager.getInstance().saveAllDocuments();
        check(project);
    }

    @Override
    public void update(AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        StudyUtils.updateAction(e);
        if (presentation.isEnabled()) {
            presentation.setEnabled(!checkInProgress.get());
        }
    }

    @Override
    public String[] getShortcuts() {
        return new String[]{SHORTCUT};
    }
}
