package org.stepik.core.actions.step

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import org.stepik.core.StudyUtils.pluginId
import org.stepik.core.actions.getShortcutText
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.metrics.Metrics
import org.stepik.core.testFramework.runners.ExitCause
import org.stepik.core.testFramework.runners.Runner
import org.stepik.core.testFramework.runners.TestResult
import org.stepik.core.testFramework.toolWindow.StepikTestResultToolWindow
import org.stepik.core.testFramework.toolWindow.showTestResultsToolWindow


class TestSamplesAction : CodeQuizAction(TEXT, DESCRIPTION, AllIcons.Actions.Resume) {

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val stepNode = getCurrentCodeStepNode(project) ?: return
        val runner = stepNode.currentLang.runner
        val title = "${stepNode.parent?.name ?: "Lesson"} : ${stepNode.name}"
        val resultWindow = showTestResultsToolWindow(project, title)

        getApplication().runWriteAction {
            VirtualFileManager.getInstance().syncRefresh()
        }

        getApplication().executeOnPooledThread {
            if (haveTests(project, stepNode)) {
                testWithTestFile(resultWindow, stepNode, runner, project)
            } else {
                testSamples(resultWindow, stepNode, runner, project)
            }
        }

        Metrics.testCodeAction(project, stepNode)
    }

    private fun haveTests(project: Project, stepNode: StepNode): Boolean {
        val stepDirectory = project.baseDir.findFileByRelativePath(stepNode.path)
        val language = stepNode.currentLang

        return stepDirectory?.findFileByRelativePath(
                listOf("tests", language.langName, language.testFileName).joinToString("/")
        ) != null
    }

    private fun testWithTestFile(resultWindow: StepikTestResultToolWindow, stepNode: StepNode, runner: Runner, project: Project) {
        resultWindow.apply {
            clear()
            println("Test method: test file")
        }
        val result = runner.testFiles(project, stepNode)
        val status = getStatusString(result)
        resultWindow.println(status)
        if (!result.passed && result.cause == ExitCause.WRONG) {
            resultWindow.apply {
                println("Return:\n${result.actual}")
                println("Error:\n${result.errorString}\n")
            }
        }
        resultWindow.println("Done")
    }

    private fun testSamples(resultWindow: StepikTestResultToolWindow,
                            stepNode: StepNode,
                            runner: Runner,
                            project: Project,
                            testClass: Boolean = false) {
        resultWindow.apply {
            clear()
            println("Test method: samples")
            if (stepNode.samples.isEmpty()) {
                println("This step doesn't contain samples")
                return
            }
        }

        stepNode.samples.forEachIndexed { index, sample ->
            resultWindow.print("Sample #${index + 1} ")
            val result = runner.testSamples(project, stepNode, sample.input,
                    { it.trimEnd() == sample.output.trimEnd() },
                    testClass = testClass)
            val status = getStatusString(result)

            resultWindow.println(status)
            if (!result.passed && result.cause == ExitCause.WRONG) {
                resultWindow.apply {
                    println("Input:\n${sample.input}")
                    println("Expected:\n${sample.output}")
                    println("Actual:\n${result.actual}")
                    println("Error:\n${result.errorString}\n")
                }
            }
        }
        resultWindow.println("Done")
    }

    private fun getStatusString(result: TestResult): String {
        return when {
            result.passed -> "PASSED"
            else -> when (result.cause) {
                ExitCause.TIME_LIMIT -> "FAIL (time left)"
                ExitCause.NO_CREATE_PROCESS -> "FAIL (can't create the test process)"
                else -> "FAIL"
            }
        }
    }

    override fun update(e: AnActionEvent?) {
        super.update(e)
        val presentation = e?.presentation ?: return
        if (!presentation.isEnabled) {
            return
        }

        val project = e.project ?: return
        val stepNode = getCurrentCodeStepNode(project) ?: return

        presentation.isEnabled = haveTests(project, stepNode) || stepNode.samples.isNotEmpty()
    }

    companion object {
        private val ACTION_ID = "$pluginId.TestSamplesAction"
        private const val SHORTCUT = "ctrl shift pressed F10"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Test solution locally ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Test solution locally"
    }
}
