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

    fun createTestProcess(project: Project, stepNode: StepNode): TestProcess? {
        return null
    }

    fun test(project: Project,
             stepNode: StepNode,
             input: String,
             assertion: (String) -> Boolean): TestResult {
        val process = createTestProcess(project, stepNode)?.start()
                ?: return TestResult(false, "", ExitCause.NO_CREATE_PROCESS)

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
            return TestResult(false, "", ExitCause.TIME_LIMIT)
        }

        val actual = actualOutput.toString()
        return TestResult(assertion(actual), actual, ExitCause.NOTHING)
    }
}

data class TestResult(val passed: Boolean, val actual: String, val cause: ExitCause)

enum class ExitCause {
    NOTHING,
    TIME_LIMIT,
    NO_CREATE_PROCESS
}
