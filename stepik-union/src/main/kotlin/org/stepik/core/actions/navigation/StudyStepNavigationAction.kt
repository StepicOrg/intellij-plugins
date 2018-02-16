package org.stepik.core.actions.navigation

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import org.stepik.core.StepikProjectManager
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.utils.NavigationUtils
import javax.swing.Icon


abstract class StudyStepNavigationAction(text: String?,
                                         description: String?,
                                         icon: Icon?) :
        StudyActionWithShortcut(text, description, icon) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        navigateStep(project)
    }

    private fun navigateStep(project: Project) {
        var currentNode = StepikProjectManager.getSelected(project)
        if (currentNode == null) {
            currentNode = StepikProjectManager.getProjectRoot(project)
        }

        val targetNode: StudyNode<*, *>?

        targetNode = getTargetStep(currentNode)

        if (targetNode == null) {
            return
        }

        NavigationUtils.navigate(project, targetNode)
    }

    protected abstract fun getTargetStep(sourceStepNode: StudyNode<*, *>?): StudyNode<*, *>?

    override fun update(e: AnActionEvent?) {
        val presentation = e!!.presentation
        presentation.isEnabled = false

        val project = e.project!!
        if (!StepikProjectManager.isStepikProject(project)) {
            return
        }

        val selected = StepikProjectManager.getSelected(project)
        val target = getTargetStep(selected)
        var enabled = selected == null || target != null

        if (StepikProjectManager.isAdaptive(project)) {
            enabled = enabled && selected != null && target!!.parent === selected.parent
        }
        presentation.isEnabled = enabled
    }
}
