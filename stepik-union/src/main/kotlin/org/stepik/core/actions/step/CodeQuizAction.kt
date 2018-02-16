package org.stepik.core.actions.step

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Contract
import org.stepik.core.actions.step.AbstractStepAction
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StepType.CODE
import javax.swing.Icon

abstract class CodeQuizAction protected constructor(
        text: String?,
        description: String?,
        icon: Icon?) : AbstractStepAction(text, description, icon) {

    override fun update(e: AnActionEvent) {
        super.update(e)
        val presentation = e.presentation

        if (!presentation.isEnabled) {
            return
        }

        val stepNode = AbstractStepAction.Companion.getCurrentStep(e.project)
        presentation.isEnabled = stepNode != null && stepNode.type == CODE
    }

    companion object {

        @Contract("null -> null")
        fun getCurrentCodeStepNode(project: Project?): StepNode? {
            val stepNode = AbstractStepAction.Companion.getCurrentStep(project)
            return if (stepNode == null || stepNode.type != CODE) {
                null
            } else stepNode
        }
    }
}
