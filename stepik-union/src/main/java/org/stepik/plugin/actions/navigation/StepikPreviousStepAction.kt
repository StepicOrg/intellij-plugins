package org.stepik.plugin.actions.navigation

import com.intellij.icons.AllIcons
import org.stepik.core.actions.getShortcutText
import org.stepik.core.actions.navigation.StepikStepNavigationAction
import org.stepik.core.courseFormat.StudyNode

class StepikPreviousStepAction : StepikStepNavigationAction(TEXT, DESCRIPTION, AllIcons.Actions.Back) {

    override fun getTargetStep(sourceStepNode: StudyNode<*, *>?): StudyNode<*, *>? {
        return StudyNavigator.previousLeaf(sourceStepNode)
    }

    override fun getActionId(): String {
        return ACTION_ID
    }

    override fun getShortcuts(): Array<String>? {
        return arrayOf(SHORTCUT)
    }

    companion object {
        private const val ACTION_ID = "STEPIK.PreviousStepAction"
        private const val SHORTCUT = "ctrl pressed COMMA"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Previous Step ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Navigate to the previous step"
    }
}
