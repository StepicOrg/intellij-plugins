package org.stepik.plugin.actions.navigation

import com.intellij.icons.AllIcons
import org.stepik.core.actions.getShortcutText
import org.stepik.core.actions.navigation.StepikStepNavigationAction
import org.stepik.core.courseFormat.StudyNode

class StepikNextStepAction : StepikStepNavigationAction(TEXT, DESCRIPTION, AllIcons.Actions.Forward) {

    override fun getTargetStep(sourceStepNode: StudyNode<*, *>?): StudyNode<*, *>? {
        return StudyNavigator.nextLeaf(sourceStepNode)
    }

    override fun getActionId(): String {
        return ACTION_ID
    }

    override fun getShortcuts(): Array<String>? {
        return arrayOf(SHORTCUT)
    }

    companion object {
        private const val ACTION_ID = "STEPIK.NextStepAction"
        private const val SHORTCUT = "ctrl pressed PERIOD"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Next Step ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Navigate to the next step"
    }
}
