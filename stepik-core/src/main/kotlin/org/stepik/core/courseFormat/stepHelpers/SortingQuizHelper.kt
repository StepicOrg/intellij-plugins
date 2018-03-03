package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode


class SortingQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {
    private var ordering: List<Pair<Int, String>>? = null

    fun getOrdering(): List<Pair<Int, String>> {
        initStepOptions()
        return ordering!!
    }

    override fun done() {
        val replyOrdering = reply.ordering
        val values = dataset.options

        ordering = if (replyOrdering.size != values.size) {
            0 until values.size
        } else {
            replyOrdering
        }.map { it to values.getOrElse(it, { "" }) }
    }

    override fun fail() {
        ordering = emptyList()
    }
}
