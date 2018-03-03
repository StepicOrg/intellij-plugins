package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode


open class StringQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {

    val text: String
        get() {
            initStepOptions()
            return reply.text
        }

    val isTextDisabled: Boolean
        get() {
            initStepOptions()
            return dataset.isTextDisabled
        }
}
