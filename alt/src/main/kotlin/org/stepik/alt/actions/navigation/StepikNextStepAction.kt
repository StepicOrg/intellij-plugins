package org.stepik.alt.actions.navigation

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.ex.MessagesEx
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.actions.getShortcutText
import org.stepik.core.actions.navigation.StudyNavigator
import org.stepik.core.actions.navigation.StudyStepNavigationAction
import org.stepik.core.courseFormat.LessonNode
import org.stepik.core.courseFormat.Node
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.stepik.StepikAuthManager.authAndGetStepikApiClient

class StepikNextStepAction : StudyStepNavigationAction(TEXT, DESCRIPTION, AllIcons.Actions.Forward) {

    override fun getTargetStep(project: Project, currentStepNode: StudyNode?): StudyNode? {
        var lesson: LessonNode? = null
        val initialValue = "https://alt.stepik.org/topics/lesson/" + when (currentStepNode) {
            is StepNode -> currentStepNode.parent?.id ?: ""
            is LessonNode -> currentStepNode.id
            else -> ""
        }
        val input = MessagesEx.showInputDialog("Example, https://alt.stepik.org/topics/lesson/50509",
                "Input link to lesson", null, initialValue, object : InputValidator {
            override fun checkInput(value: String?): Boolean {
                return value != null && template.matchEntire(value) != null
            }

            override fun canClose(value: String?): Boolean {
                return true
            }

        })
        if (input != null) {
            val matcher = template.matchEntire(input)
            if (matcher != null) {
                val lessonId = matcher.groups[1]!!.value
                val projectManager = getProjectManager(project)
                val stepikApiClient = authAndGetStepikApiClient()
                val root = projectManager?.projectRoot
                lesson = LessonNode(project, stepikApiClient)
                lesson.id = lessonId.toLong()
                lesson.parent = root
                val children = root?.children?.toMutableList()
                if (children != null) {
                    children.add(lesson)
                    lesson.reloadData(project, stepikApiClient)
                    (root as Node).setChildren(children)
                    projectManager.refreshProjectFiles()
                }
            }
        }

        return StudyNavigator.nextLeaf(lesson)
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun update(e: AnActionEvent?) {
        val presentation = e?.presentation ?: return
        presentation.isEnabled = true
    }

    companion object {
        private const val ACTION_ID = "Alt.NextStepAction"
        private const val SHORTCUT = "ctrl pressed PERIOD"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Next Step ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Navigate to the next step"
        private val template = ".*/lesson/(\\d+)".toRegex()

        fun getNextStep(): StudyNode? {
            return StudyNavigator.nextLeaf(null)
        }
    }
}
