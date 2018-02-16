package org.stepik.plugin.actions.navigation

import com.intellij.openapi.project.Project
import org.stepik.core.StepikProjectManager
import org.stepik.core.actions.navigation.StudyStepNavigationAction
import org.stepik.core.courseFormat.StudyNode
import javax.swing.Icon

abstract class StepikNavigationAction(text: String?, description: String?, icon: Icon?) :
        StudyStepNavigationAction(text, description, icon) {

    override fun isEnabled(project: Project, enabled: Boolean, selected: StudyNode<*, *>?, target: StudyNode<*, *>?): Boolean {
        if (StepikProjectManager.isAdaptive(project)) {
            return enabled && selected != null && target?.parent === selected.parent
        }
        return enabled
    }
}
