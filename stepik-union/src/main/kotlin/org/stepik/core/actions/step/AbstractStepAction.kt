package org.stepik.core.actions.step

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Contract
import org.stepik.core.StepikProjectManager
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.courseFormat.StepNode
import javax.swing.Icon


abstract class AbstractStepAction protected constructor(text: String?,
                                                        description: String?,
                                                        icon: Icon?) :
        StudyActionWithShortcut(text, description, icon) {

    override fun update(e: AnActionEvent) {
        val stepNode = getCurrentStep(e.project)
        e.presentation.isEnabled = stepNode != null && !stepNode.wasDeleted
    }

    companion object {

        @Contract("null -> null")
        fun getCurrentStep(project: Project?): StepNode? {
            val studyNode = StepikProjectManager.getSelected(project)
            return studyNode as? StepNode
        }
    }
}
