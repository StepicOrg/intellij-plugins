package org.stepik.core.testFramework.runners

import com.intellij.execution.RunManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.processes.TestProcess
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.PrintStream
import java.util.concurrent.TimeUnit

interface Runner {

    fun updateRunConfiguration(project: Project, stepNode: StepNode) {
        ApplicationManager.getApplication().invokeLater {
            RunManager.getInstance(project).selectedConfiguration = null
        }
    }

    fun createTestProcess(project: Project, stepNode: StepNode): TestProcess {
        return TestProcess(project, stepNode)
    }

    fun test(project: Project,
             stepNode: StepNode,
             input: String,
             assertion: (String) -> Boolean): TestResult {
        val process = createTestProcess(project, stepNode).start() ?: return NO_PROCESS

        val outputStream = PrintStream(BufferedOutputStream(process.outputStream))
        outputStream.print("$input\n")
        outputStream.flush()
        val inputStream = BufferedInputStream(process.inputStream)
        val actualOutput = StringBuffer()
        ApplicationManager.getApplication().executeOnPooledThread {
            var b = inputStream.read()
            while (b != -1) {
                actualOutput.append(b.toChar())
                b = inputStream.read()
            }
        }

        val success = process.waitFor(stepNode.limit.time.toLong(), TimeUnit.SECONDS)

        if (!success) {
            process.destroyForcibly()
            return TIME_LEFT
        }

        val actual = actualOutput.toString()
        val score = assertion(actual)
        val cause: ExitCause
        if (score) {
            cause = ExitCause.CORRECT
        } else {
            cause = ExitCause.WRONG
        }
        return TestResult(score, actual, cause)
    }
}
