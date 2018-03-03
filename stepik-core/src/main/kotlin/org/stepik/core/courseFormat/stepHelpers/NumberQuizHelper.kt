package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode


class NumberQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {

    val number: String
        get() {
            initStepOptions()
            return reply.number
        }
}
