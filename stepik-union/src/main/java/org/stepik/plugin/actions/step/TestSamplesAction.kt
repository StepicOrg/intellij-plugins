package org.stepik.plugin.actions.step

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.stepik.core.testFramework.runners.ExitCause
import org.stepik.core.testFramework.toolWindow.StepikTestToolWindowUtils
import org.stepik.plugin.actions.ActionUtils


class TestSamplesAction : CodeQuizAction(TEXT, DESCRIPTION, DefaultRunExecutor.getRunExecutorInstance().icon) {

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val stepNode = CodeQuizAction.getCurrentCodeStepNode(project) ?: return
        val language = stepNode.currentLang
        val runner = language.runner

        val title = "${stepNode.parent?.name ?: "Lesson"} : ${stepNode.name}"
        val resultWindow = StepikTestToolWindowUtils.showTestResultsToolWindow(project, title)

        ApplicationManager.getApplication().executeOnPooledThread {
            var counter = 0
            resultWindow.clear()
            resultWindow.println("Test method: samples")
            System.out.flush()
            stepNode.samples.forEach {
                resultWindow.print("Test #${counter++} ")
                val result = runner.test(project, stepNode, it.input, { actual -> actual == it.output })
                val status: String
                if (result.passed) {
                    status = "PASSED"
                } else {
                    when (result.cause) {
                        ExitCause.TIME_LIMIT -> status = "FAIL (time left)"
                        ExitCause.NO_CREATE_PROCESS -> status = "FAIL (can't create the test process)"
                        else -> status = "FAIL"
                    }
                }
                resultWindow.println(status)
                if (!result.passed && result.cause == ExitCause.WRONG) {
                    resultWindow.println("Input:\n${it.input}")
                    resultWindow.println("Expected:\n${it.output}")
                    resultWindow.println("Actual:\n${result.actual}")
                    resultWindow.println()
                }
            }
            resultWindow.println("Done")
        }
    }

    companion object {
        private val SHORTCUT = "ctrl shift pressed F10"
        private val ACTION_ID = "STEPIK.TestSamplesAction"
        private val SHORTCUT_TEXT = ActionUtils.getShortcutText(SHORTCUT)
        private val TEXT = "Test samples ($SHORTCUT_TEXT)"
        private val DESCRIPTION = "Test samples."
    }
}
