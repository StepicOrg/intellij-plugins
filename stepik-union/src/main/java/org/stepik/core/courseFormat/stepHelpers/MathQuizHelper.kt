package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode

class MathQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {

    override fun isAutoCreateAttempt() = true
}
