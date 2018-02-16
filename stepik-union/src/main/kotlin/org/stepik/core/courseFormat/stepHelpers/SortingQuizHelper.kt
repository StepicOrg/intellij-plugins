package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import org.stepik.core.courseFormat.StepNode


class SortingQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {
    private var ordering: List<Pair<Int, String>>? = null

    fun getOrdering(): List<Pair<Int, String>> {
        initStepOptions()
        return ordering!!
    }

    override fun done() {
        var replyOrdering = reply.ordering
        val values = dataset.options

        val valuesCount = values.size

        if (replyOrdering.size != valuesCount) {
            replyOrdering = (0..valuesCount).toList()
        }
        ordering = replyOrdering.map { index -> Pair.create(index, if (index < valuesCount) values[index] else "") }
    }

    override fun fail() {
        ordering = emptyList()
    }
}
