package org.stepik.core.actions.step

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import org.stepik.core.actions.getShortcutText
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus
import org.stepik.core.testFramework.runners.ExitCause
import org.stepik.core.testFramework.runners.Runner
import org.stepik.core.testFramework.runners.TestResult
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow
import org.stepik.core.testFramework.toolWindow.StepikTestToolWindowUtils


class TestSamplesAction : CodeQuizAction(TEXT, DESCRIPTION, AllIcons.Actions.Resume) {

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val stepNode = CodeQuizAction.getCurrentCodeStepNode(project) ?: return
        val language = stepNode.currentLang
        val runner = language.runner
        val title = "${stepNode.parent?.name ?: "Lesson"} : ${stepNode.name}"
        val resultWindow = StepikTestToolWindowUtils.showTestResultsToolWindow(project, title)
        val stepDirectory = project.baseDir.findFileByRelativePath(stepNode.path)
        ApplicationManager.getApplication().runWriteAction {
            VirtualFileManager.getInstance().syncRefresh()
        }
        val haveTests = stepDirectory?.findFileByRelativePath(listOf("tests", language.langName, language.testFileName).joinToString("/")) != null

        ApplicationManager.getApplication().executeOnPooledThread {
            if (haveTests) {
                testWithTestFile(resultWindow, stepNode, runner, project)
            } else {
                testSamples(resultWindow, stepNode, runner, project)
            }
        }

        Metrics.testCodeAction(project, stepNode, MetricsStatus.SUCCESSFUL)
    }

    private fun testWithTestFile(resultWindow: StepikTestResultToolWindow, stepNode: StepNode, runner: Runner, project: Project) {
        resultWindow.clear()
        resultWindow.println("Test method: test file")

        val result = runner.testFiles(project, stepNode)
        val status = getStatusString(result)
        resultWindow.println(status)
        if (!result.passed && result.cause == ExitCause.WRONG) {
            resultWindow.println("Return:\n${result.actual}")
            resultWindow.println("Error:\n${result.errorString}")
            resultWindow.println()
        }
        resultWindow.println("Done")
    }

    private fun testSamples(resultWindow: StepikTestResultToolWindow,
                            stepNode: StepNode,
                            runner: Runner,
                            project: Project,
                            testClass: Boolean = false) {
        var counter = 0
        resultWindow.clear()
        resultWindow.println("Test method: samples")

        stepNode.samples.forEach {
            resultWindow.print("Test #${counter++} ")
            val result = runner.testSamples(project, stepNode, it.input, { actual -> actual == it.output }, testClass = testClass)
            val status = getStatusString(result)

            resultWindow.println(status)
            if (!result.passed && result.cause == ExitCause.WRONG) {
                resultWindow.println("Input:\n${it.input}")
                resultWindow.println("Expected:\n${it.output}")
                resultWindow.println("Actual:\n${result.actual}")
                resultWindow.println("Error:\n${result.errorString}")
                resultWindow.println()
            }
        }
        resultWindow.println("Done")
    }

    private fun getStatusString(result: TestResult): String {
        return if (result.passed) {
            "PASSED"
        } else {
            when (result.cause) {
                ExitCause.TIME_LIMIT -> "FAIL (time left)"
                ExitCause.NO_CREATE_PROCESS -> "FAIL (can't create the test process)"
                else -> "FAIL"
            }
        }
    }

    companion object {
        private const val SHORTCUT = "ctrl shift pressed F10"
        private const val ACTION_ID = "STEPIK.TestSamplesAction"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Test a code (${SHORTCUT_TEXT})"
        private const val DESCRIPTION = "Test a code"
    }
}
