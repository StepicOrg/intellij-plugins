package org.stepik.core.testFramework.runners

import com.intellij.execution.RunManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.testFramework.processes.TestProcess
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

    fun test(project: Project, stepNode: StepNode, input: String, output: String): TestResult {
        val process = createTestProcess(project, stepNode)?.start() ?: return TestResult(false, "")
        process.isAlive
        val outputStream = PrintStream(process.outputStream)
        outputStream.print(input)
        outputStream.flush()
        val inputStream = process.inputStream
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
            return TestResult(false, "")
        }

        val actual = actualOutput.toString()
        return TestResult(actual == output, actual)
    }
}

data class TestResult(val passed: Boolean, val actual: String)
