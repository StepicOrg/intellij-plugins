package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import org.stepik.core.courseFormat.StepNode
import java.util.*


class MatchingQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {
    private var ordering: List<Pair<Int, Array<String>>>? = null

    override fun done() {
        val values = dataset.pairs
        var replyOrdering = reply.ordering

        if (replyOrdering.size != values.size) {
            replyOrdering = (0 until values.size).toList()
        }

        ordering = (0 until values.size)
                .map { i ->
                    val index = replyOrdering[i]
                    val captions = arrayOf(values[i].first, if (index < values.size) values[index].second else "")
                    Pair.create<Int, Array<String>>(index, captions)
                }
    }

    override fun fail() {
        ordering = ArrayList()
    }

    fun getOrdering(): List<Pair<Int, Array<String>>> {
        initStepOptions()
        return ordering!!
    }
}
