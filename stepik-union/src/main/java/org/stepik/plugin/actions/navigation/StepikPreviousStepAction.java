package org.stepik.plugin.actions.navigation;

import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.plugin.actions.ActionUtils;

public class StepikPreviousStepAction extends StepikStepNavigationAction {
    private static final String ACTION_ID = "STEPIK.PreviousStepAction";
    private static final String SHORTCUT = "ctrl pressed COMMA";
    private static final String SHORTCUT_TEXT = ActionUtils.getShortcutText(SHORTCUT);
    private static final String TEXT = "Previous Step (" + SHORTCUT_TEXT + ")";
    private static final String DESCRIPTION = "Navigate to the previous step";

    public StepikPreviousStepAction() {
        super(TEXT, DESCRIPTION, AllIcons.Actions.Back);
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
