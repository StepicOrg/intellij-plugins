package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode


class TableQuizHelper(project: Project, stepNode: StepNode) : QuizHelper(project, stepNode) {
    private var choices: Map<String, Map<String, Boolean>>? = null

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

        choices = if (tableChoices.isEmpty()) {
            dataset.rows.associate { row ->
                row to dataset.columns.associate { it to false }
            }
        } else {
            tableChoices.associate {
                it.nameRow to it.columns.associate { it.name to it.answer }
            }
        }
    }

    override fun fail() {
        choices = emptyMap()
    }

    fun getChoice(rowName: String, colName: String): Boolean {
        initStepOptions()
        return choices?.get(rowName)?.get(colName) == true
    }
}
