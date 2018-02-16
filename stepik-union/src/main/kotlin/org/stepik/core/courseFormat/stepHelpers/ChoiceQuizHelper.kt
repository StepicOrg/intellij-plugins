package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import org.stepik.core.courseFormat.StepNode
import java.util.*


class ChoiceQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {
    private var stepOptions: List<Pair<String, Boolean>>? = null

    val options: List<Pair<String, Boolean>>
        get() {
            initStepOptions()
            return stepOptions!!
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
        stepOptions = (0..options.size)
                .map { i -> Pair.create(options[i], choices[i]) }
    }

    internal override fun fail() {
        stepOptions = ArrayList()
    }
}
