package org.stepik.hyperskill.actions.navigation

import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.ex.MessagesEx.showInputDialog
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.core.actions.getShortcutText
import org.stepik.core.actions.navigation.StudyNavigator
import org.stepik.core.actions.navigation.StudyStepNavigationAction
import org.stepik.core.auth.StepikAuthManager.authAndGetStepikApiClient
import org.stepik.core.courseFormat.LessonNode
import org.stepik.core.courseFormat.Node
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.getProjectManager
import org.stepik.core.isStepikProject
import org.stepik.core.pluginId

class OpenProblemAction : StudyStepNavigationAction(TEXT, DESCRIPTION, AllIcons.Actions.Forward) {
    
    override fun getTargetStep(project: Project, currentStepNode: StudyNode?): StudyNode? {
        return loadProblem(project, currentStepNode)
    }
    
    override fun getActionId() = ACTION_ID
    
    override fun getShortcuts() = arrayOf(SHORTCUT)
    
    override fun update(e: AnActionEvent?) {
        e?.presentation?.isEnabled = isStepikProject(e?.project)
    }
    
    companion object {
        internal val ACTION_ID = "$pluginId.OpenProblemAction"
        private const val SHORTCUT = "ctrl pressed PERIOD"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Open problem ($SHORTCUT_TEXT)"
        internal const val DESCRIPTION = "Open problem"
        private val template = "(?:.*/lesson/|)(\\d+)".toRegex()
        private const val exampleLink = "https://hyperskill.org/learn/lesson/"
        private const val exampleLessonId = "58110"
        
        private fun inputLink(studyNode: StudyNode?): String? {
            val id = when (studyNode) {
                         is StepNode   -> studyNode.parent?.id
                         is LessonNode -> studyNode.id
                         else          -> null
                     } ?: exampleLessonId
            
            val initialValue = "https://hyperskill.org/learn/lesson/$id"
            
            var link: String? = null
            
            getApplication().invokeAndWait {
                link = showInputDialog("Example: $exampleLink$exampleLessonId", "Paste a link to lesson",
                        null, initialValue, object : InputValidator {
                    override fun checkInput(value: String): Boolean {
                        return template.matchEntire(value) != null
                    }
                    
                    override fun canClose(value: String) = true
                    
                })
            }
            
            return link
        }
        
        fun loadProblem(project: Project, currentStepNode: StudyNode?): StudyNode? {
            val link = inputLink(currentStepNode)
            
            var lesson: LessonNode? = null
            
            if (link != null) {
                val matcher = template.matchEntire(link)
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
                
                if (lesson == null || (lesson.data as CompoundUnitLesson).lesson.steps.isEmpty()) {
                    Notification("hyperskill.CantOpenProblem", "Can not open problem",
                            "Can not open problem: $link", NotificationType.ERROR).notify(project)
                }
            }
            
            return StudyNavigator.nextLeaf(lesson)
        }
    }
}
