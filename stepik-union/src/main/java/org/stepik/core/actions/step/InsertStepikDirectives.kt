package org.stepik.core.actions.step

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import org.stepik.core.StepikProjectManager
import org.stepik.core.actions.getShortcutText
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.SUCCESSFUL
import org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory
import org.stepik.core.utils.Utils
import org.stepik.core.utils.containsDirectives
import org.stepik.core.utils.getFileText
import org.stepik.core.utils.insertAmbientCode
import org.stepik.core.utils.removeAmbientCode
import org.stepik.core.utils.writeInToFile
import org.stepik.plugin.utils.ReformatUtils


class InsertStepikDirectives : CodeQuizAction(TEXT, DESCRIPTION, AllIcons.General.ExternalToolsSmall) {

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val stepNode = getCurrentCodeStepNode(project) ?: return

        Utils.saveAllDocuments(project!!)

        val currentLang = stepNode.currentLang

        val src = getOrCreateSrcDirectory(project, stepNode, true) ?: return

        val file = src.findChild(currentLang.mainFileName) ?: return

        var text = getFileText(file)

        val projectManager = StepikProjectManager.getInstance(project)
        val showHint = projectManager != null && projectManager.showHint
        val needInsert = !containsDirectives(text, currentLang)
        if (needInsert) {
            text = insertAmbientCode(text, currentLang, showHint)
            Metrics.insertAmbientCodeAction(project, stepNode, SUCCESSFUL)
        } else {
            text = removeAmbientCode(text, showHint, currentLang, true)
            Metrics.removeAmbientCodeAction(project, stepNode, SUCCESSFUL)
        }
        writeInToFile(text, file, project)
        if (needInsert) {
            val document = FileDocumentManager.getInstance().getDocument(file)
            if (document != null) {
                ReformatUtils.reformatSelectedEditor(project, document)
            }
        }

        currentLang.runner.updateRunConfiguration(project, stepNode)
    }

    companion object {
        private const val SHORTCUT = "ctrl alt pressed R"
        private const val ACTION_ID = "STEPIK.InsertStepikDirectives"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Repair standard template ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Insert Stepik directives. Repair ordinary template if it is possible."
    }
}
