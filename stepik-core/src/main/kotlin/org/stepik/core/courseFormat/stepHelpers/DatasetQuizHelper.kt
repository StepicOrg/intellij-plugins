package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode


class DatasetQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {

    val data: String
        get() {
            initStepOptions()
            return reply.file
        }

    override val isAutoCreateAttempt: Boolean
        get() = false
}
