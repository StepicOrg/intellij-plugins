package org.stepik.plugin.actions.navigation;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepikNextStepAction extends StepikStepNavigationAction {
    private static final String ACTION_ID = "STEPIK.NextStepAction";
    private static final String SHORTCUT = "ctrl pressed PERIOD";

    public StepikNextStepAction() {
        super("Next Step (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT),
                null)) + ")", "Navigate to the next step", AllIcons.Actions.Forward);
    }

    @Override
    protected StudyNode getTargetStep(@Nullable final StudyNode sourceStepNode) {
        return StudyNavigator.nextLeaf(sourceStepNode);
    }

    @NotNull
    @Override
    public String getActionId() {
        return ACTION_ID;
    }

    @Nullable
    @Override
    public String[] getShortcuts() {
        return new String[]{SHORTCUT};
    }
}
