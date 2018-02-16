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
            val stringBuilder = StringBuilder()

            val samples = stepNode.samples

            for (i in 1..samples.size) {
                val sample = samples.get(i - 1)
                stringBuilder.append("<p><b>Sample Input ")
                        .append(i)
                        .append(":</b><br>")
                        .append(sample.input)
                        .append("<br>")
                        .append("<b>Sample Output ")
                        .append(i)
                        .append(":</b><br>")
                        .append(sample.output)
                        .append("<br>")
            }

            return stringBuilder.toString().replace("\\n".toRegex(), "<br>")
        }
}
