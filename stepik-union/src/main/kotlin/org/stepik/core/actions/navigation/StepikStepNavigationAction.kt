package org.stepik.core.actions.navigation

import com.intellij.openapi.project.Project
import org.stepik.core.StepikProjectManager
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.utils.NavigationUtils
import javax.swing.Icon

abstract class StepikStepNavigationAction protected constructor(text: String?,
                                                                description: String?,
                                                                icon: Icon?) :
        StudyStepNavigationAction(text, description, icon) {

    public override fun navigateStep(project: Project) {
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
}
