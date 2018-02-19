package org.stepik.plugin.actions.navigation

import com.intellij.icons.AllIcons
import org.stepik.core.actions.getShortcutText
import org.stepik.core.courseFormat.StudyNode
import org.stepik.plugin.actions.navigation.StudyNavigator.previousLeaf

class StepikPreviousStepAction : StepikNavigationAction(TEXT, DESCRIPTION, AllIcons.Actions.Back) {

    override fun getTargetStep(currentStepNode: StudyNode<*, *>?): StudyNode<*, *>? {
        return previousLeaf(currentStepNode)
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    companion object {
        private const val ACTION_ID = "STEPIK.PreviousStepAction"
        private const val SHORTCUT = "ctrl pressed COMMA"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Previous Step ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Navigate to the previous step"
    }
}
