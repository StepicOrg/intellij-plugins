package org.stepik.plugin.actions.step;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import icons.AllStepikIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class StudyCheckAction extends AbstractStepAction {
    private static final String SHORTCUT = "ctrl alt pressed ENTER";

    StudyCheckAction() {
        super("Check Step (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT),
                null)) + ")", "Check current step", AllStepikIcons.ToolWindow.checkTask);
    }

    protected abstract void check(@NotNull final Project project);

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
    public String[] getShortcuts() {
        return new String[]{SHORTCUT};
    }
}
