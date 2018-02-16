package org.stepik.core.actions.navigation

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import org.stepik.core.ProjectManager
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
        val projectManager = ServiceManager.getService(project, ProjectManager::class.java)
        val currentNode = projectManager.getSelected() ?: projectManager.getProjectRoot()
        val targetNode = getTargetStep(currentNode) ?: return
        NavigationUtils.navigate(project, targetNode)
    }

    protected abstract fun getTargetStep(currentStepNode: StudyNode<*, *>?): StudyNode<*, *>?

    override fun update(e: AnActionEvent?) {
        val presentation = e?.presentation ?: return
        val project = e.project ?: return
        presentation.isEnabled = false

        val projectManager = ServiceManager.getService(project, ProjectManager::class.java)

        if (!projectManager.isStepikProject(project)) {
            return
        }

        val selected = projectManager.getSelected(project)
        val target = getTargetStep(selected)
        val enabled = selected == null || target != null
        presentation.isEnabled = isEnabled(project, enabled, selected, target)
    }

    open fun isEnabled(project: Project, enabled: Boolean,
                       selected: StudyNode<*, *>?, target: StudyNode<*, *>?): Boolean = enabled
}
