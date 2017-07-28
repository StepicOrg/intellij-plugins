package org.stepik.plugin.actions.navigation;

import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.plugin.actions.ActionUtils;

public class StepikNextStepAction extends StepikStepNavigationAction {
    private static final String ACTION_ID = "STEPIK.NextStepAction";
    private static final String SHORTCUT = "ctrl pressed PERIOD";
    private static final String SHORTCUT_TEXT = ActionUtils.getShortcutText(SHORTCUT);
    private static final String TEXT = "Next Step (" + SHORTCUT_TEXT + ")";
    private static final String DESCRIPTION = "Navigate to the next step";

    public StepikNextStepAction() {
        super(TEXT, DESCRIPTION, AllIcons.Actions.Forward);
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
