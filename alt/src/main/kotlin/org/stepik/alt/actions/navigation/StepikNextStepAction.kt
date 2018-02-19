package org.stepik.alt.actions.navigation

import com.intellij.icons.AllIcons
import org.stepik.core.actions.getShortcutText
import org.stepik.core.actions.navigation.StudyStepNavigationAction
import org.stepik.core.courseFormat.StudyNode

class StepikNextStepAction : StudyStepNavigationAction(TEXT, DESCRIPTION, AllIcons.Actions.Forward) {

    override fun getTargetStep(currentStepNode: StudyNode<*, *>?): StudyNode<*, *>? {
        return getNextStep()
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    companion object {
        private const val ACTION_ID = "Alt.NextStepAction"
        private const val SHORTCUT = "ctrl pressed PERIOD"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Next Step ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Navigate to the next step"

        fun getNextStep() : StudyNode<*, *>? {
            return null
        }
    }
}
