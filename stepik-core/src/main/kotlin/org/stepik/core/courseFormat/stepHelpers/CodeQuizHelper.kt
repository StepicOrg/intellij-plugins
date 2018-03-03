package org.stepik.core.courseFormat.stepHelpers

import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode


class CodeQuizHelper(project: Project, stepNode: StepNode) : StepHelper(project, stepNode) {

    val timeLimit: Int
        get() = stepNode.limit.time

    val memoryLimit: Int
        get() = stepNode.limit.memory

    val samples: String
        get() {
            val showIndex = stepNode.samples.size > 1
            return stepNode.samples.mapIndexed { i, sample ->
                val index = if (showIndex) " ${i + 1}" else ""
                return@mapIndexed "<p><b>Sample Input$index:</b><br>${sample.input}<br>" +
                        "<b>Sample Output$index:</b><br>${sample.output}"
            }.joinToString("<br>").replace("\\n".toRegex(), "<br>")
        }
}
