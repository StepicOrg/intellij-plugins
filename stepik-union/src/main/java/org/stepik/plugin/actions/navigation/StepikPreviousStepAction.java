package org.stepik.plugin.actions.navigation;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import org.stepik.core.courseFormat.StudyNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepikPreviousStepAction extends StepikStepNavigationAction {
    private static final String ACTION_ID = "STEPIK.PreviousStepAction";
    private static final String SHORTCUT = "ctrl pressed COMMA";

    public StepikPreviousStepAction() {
        super("Previous Step (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT),
                null)) + ")", "Navigate to the previous step", AllIcons.Actions.Back);
    }

    @Override
    protected StudyNode getTargetStep(@Nullable final StudyNode sourceStepNode) {
        return StudyNavigator.previousLeaf(sourceStepNode);
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
