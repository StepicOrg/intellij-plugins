package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode
import java.util.*


class ChoiceQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {
    private var stepOptions: List<Pair<String, Boolean>>? = null

    val options: List<Pair<String, Boolean>>
        get() {
            initStepOptions()
            return stepOptions ?: emptyList()
        }

    val isMultipleChoice: Boolean
        get() {
            initStepOptions()
            return dataset.isMultipleChoice
        }

    override fun done() {
        var choices = reply.choices
        val options = dataset.options
        if (choices.size != options.size) {
            choices = Collections.nCopies(options.size, false)
        }
        stepOptions = (0 until options.size).map { i -> options[i] to choices[i] }
    }

    override fun fail() {
        stepOptions = emptyList()
    }
}
