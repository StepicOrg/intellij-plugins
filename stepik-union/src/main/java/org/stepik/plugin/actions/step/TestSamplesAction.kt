package org.stepik.plugin.actions.step

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.stepik.plugin.actions.ActionUtils


class TestSamplesAction : CodeQuizAction(TEXT, DESCRIPTION, DefaultRunExecutor.getRunExecutorInstance().icon) {

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val stepNode = CodeQuizAction.getCurrentCodeStepNode(project) ?: return
        val language = stepNode.currentLang
        val runner = language.runner

        ApplicationManager.getApplication().executeOnPooledThread {
            var counter = 0
            println("Start test samples")
            System.out.flush()
            stepNode.samples.forEach {
                print("Test #${counter++} ")
                val result = runner.test(project, stepNode, it.input, it.output)
                val status: String
                if (result.passed) {
                    status = "PASSED"
                } else {
                    status = "FAIL"
                }
                System.out.println(status)
                if (!result.passed) {
                    println("Input: ${it.input}")
                    println("Expected: ${it.output}")
                    println("Actual: ${result.actual}")
                    println()
                }
            }
            println("--------------------------------")
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
