package org.stepik.core.actions.navigation

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.utils.navigate
import javax.swing.Icon


abstract class StudyStepNavigationAction(text: String?,
                                         description: String?,
                                         icon: Icon?) :
        StudyActionWithShortcut(text, description, icon) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val currentNode = getProjectManager(project)?.run { selected ?: projectRoot }
        val targetNode = getTargetStep(project, currentNode) ?: return
        navigate(project, targetNode)
    }

    protected abstract fun getTargetStep(project: Project, currentStepNode: StudyNode?): StudyNode?

    override fun update(e: AnActionEvent?) {
        val presentation = e?.presentation ?: return
        val project = e.project ?: return
        presentation.isEnabled = false

        val projectManager = getProjectManager(project) ?: return
        val selected = projectManager.selected
        val target = getTargetStep(project, selected)
        val enabled = projectManager.projectRoot != null && (selected == null || target != null)
        presentation.isEnabled = isEnabled(project, enabled, selected, target)
    }

    open fun isEnabled(project: Project, enabled: Boolean,
                       selected: StudyNode?, target: StudyNode?): Boolean = enabled
}
