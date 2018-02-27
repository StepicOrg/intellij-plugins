package org.stepik.core.actions.step

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.problems.WolfTheProblemSolver
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.actions.getShortcutText
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.metrics.Metrics
import org.stepik.core.utils.getOrCreateSrcDirectory

class StepikResetStepAction : CodeQuizAction(TEXT, DESCRIPTION, AllStepikIcons.ToolWindow.resetTaskFile) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        reset(project)
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    companion object {
        private const val ACTION_ID = "STEPIK.ResetStepAction"
        private const val SHORTCUT = "ctrl shift pressed X"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Reset to default template ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Reset to default template"

        private fun reset(project: Project) {
            val application = getApplication()
            application.invokeLater {
                application.runWriteAction { resetFile(project) }
            }
        }

        private fun resetFile(project: Project) {
            val stepNode = getCurrentCodeStepNode(project) ?: return

            val src = getOrCreateSrcDirectory(project, stepNode, true) ?: return

            val mainFileName = stepNode.currentLang.mainFileName
            val mainFile = src.findChild(mainFileName)

            if (mainFile != null) {
                val documentManager = FileDocumentManager.getInstance()
                val document = documentManager.getDocument(mainFile)
                if (document != null) {
                    resetDocument(project, document, stepNode)
                    if (!project.isDisposed) {
                        ProjectView.getInstance(project).refresh()
                        WolfTheProblemSolver.getInstance(project).clearProblems(mainFile)
                    }
                    getProjectManager(project)?.updateSelection()
                }
            }
        }

        private fun resetDocument(project: Project, document: Document, stepNode: StepNode) {
            CommandProcessor.getInstance().executeCommand(project,
                    {
                        getApplication().runWriteAction {
                            document.setText(stepNode.currentTemplate)
                            Metrics.resetStepAction(project, stepNode)
                            stepNode.currentLang.runner.updateRunConfiguration(project, stepNode)
                        }
                    },
                    DESCRIPTION, DESCRIPTION
            )
        }
    }
}
