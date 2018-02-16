package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode
import java.util.*


class TableQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {
    private var choices: MutableMap<String, Map<String, Boolean>>? = null

    val description: String
        get() {
            initStepOptions()
            return dataset.description
        }

    val rows: List<String>
        get() {
            initStepOptions()
            return dataset.rows
        }

    val columns: List<String>
        get() {
            initStepOptions()
            return dataset.columns
        }

    val isCheckbox: Boolean
        get() {
            initStepOptions()
            return dataset.isCheckbox
        }

    override fun done() {
        val tableChoices = reply.tableChoices
        choices = HashMap()

        if (tableChoices.isEmpty()) {
            val rows = dataset.rows
            val cols = dataset.columns
            for (row in rows) {
                val map = HashMap<String, Boolean>()
                for (column in cols) {
                    map[column] = false
                }
                choices!![row] = map
            }
            return
        }

        for (choice in tableChoices) {
            val map = HashMap<String, Boolean>()
            for (column in choice.columns) {
                map[column.name] = column.answer
            }
            choices!![choice.nameRow] = map
        }
    }

    override fun fail() {
        choices = HashMap()
    }

    fun getChoice(rowName: String, colName: String): Boolean {
        initStepOptions()
        return choices!!.getOrDefault(rowName, HashMap()).getOrDefault(colName, false)
    }
}
