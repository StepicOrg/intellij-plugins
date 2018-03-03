package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode


class MatchingQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {
    private var ordering: List<Pair<Int, List<String>>>? = null

    override fun done() {
        val values = dataset.pairs
        var replyOrdering = reply.ordering

        if (replyOrdering.size != values.size) {
            replyOrdering = (0 until values.size).toList()
        }

        ordering = (0 until values.size)
                .map { i ->
                    val index = replyOrdering[i]
                    index to listOf(values[i].first, values.getOrNull(index)?.second ?: "")
                }
    }

    override fun fail() {
        ordering = emptyList()
    }

    fun getOrdering(): List<Pair<Int, List<String>>> {
        initStepOptions()
        return ordering ?: emptyList()
    }
}
