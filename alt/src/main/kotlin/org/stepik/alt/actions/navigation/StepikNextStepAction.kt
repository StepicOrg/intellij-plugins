package org.stepik.alt.actions.navigation

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.ex.MessagesEx.showInputDialog
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.StudyUtils.isStepikProject
import org.stepik.core.actions.getShortcutText
import org.stepik.core.actions.navigation.StudyNavigator
import org.stepik.core.actions.navigation.StudyStepNavigationAction
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.courseFormat.LessonNode
import org.stepik.core.courseFormat.Node
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode

class StepikNextStepAction : StudyStepNavigationAction(TEXT, DESCRIPTION, AllIcons.Actions.Forward) {

    override fun getTargetStep(project: Project, currentStepNode: StudyNode?): StudyNode? {
        val input = inputLink(currentStepNode)

        var lesson: LessonNode? = null

        if (input != null) {
            val matcher = template.matchEntire(input)
            if (matcher != null) {
                val lessonId = matcher.groupValues[1].toLong()
                val projectManager = getProjectManager(project)
                val stepikApiClient = authAndGetStepikApiClient()
                val root = projectManager?.projectRoot
                lesson = LessonNode(project, stepikApiClient)
                lesson.id = lessonId
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

    private fun inputLink(studyNode: StudyNode?): String? {
        val id = when (studyNode) {
            is StepNode -> studyNode.parent?.id
            is LessonNode -> studyNode.id
            else -> null
        } ?: exampleLessonId

        val initialValue = "https://alt.stepik.org/topics/lesson/$id"

        return showInputDialog("Example, $exampleLink$exampleLessonId", "Input link to lesson",
                null, initialValue, object : InputValidator {
            override fun checkInput(value: String): Boolean {
                return template.matchEntire(value) != null
            }

            override fun canClose(value: String) = true

        })
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun update(e: AnActionEvent?) {
        e?.presentation?.isEnabled = isStepikProject(e?.project)
    }

    companion object {
        private const val ACTION_ID = "Alt.NextStepAction"
        private const val SHORTCUT = "ctrl pressed PERIOD"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Load problem ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Load problem"
        private val template = "(?:.*/lesson/|)(\\d+)".toRegex()
        private const val exampleLink = "https://alt.stepik.org/topics/lesson/"
        private const val exampleLessonId = "58110"

        fun getNextStep(): StudyNode? {
            return StudyNavigator.nextLeaf(null)
        }
    }
}
