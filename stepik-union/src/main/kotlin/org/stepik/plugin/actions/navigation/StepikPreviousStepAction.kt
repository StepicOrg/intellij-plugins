package org.stepik.plugin.actions.navigation

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import org.stepik.core.actions.getShortcutText
import org.stepik.core.actions.navigation.StudyNavigator.previousLeaf
import org.stepik.core.courseFormat.StudyNode

class StepikPreviousStepAction : StepikNavigationAction(TEXT, DESCRIPTION, AllIcons.Actions.Back) {

    override fun getTargetStep(project: Project, currentStepNode: StudyNode?): StudyNode? {
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
