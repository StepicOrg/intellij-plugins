package org.hyperskill.actions

import com.intellij.ide.highlighter.ModuleFileType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.util.io.FileUtil
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.actions.getShortcutText
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.isStepikProject
import org.stepik.core.pluginId
import org.hyperskill.projectWizard.StepikProjectGenerator
import com.intellij.openapi.project.ex.ProjectManagerEx as AppProjectManager

class StartHyperskillAction : StudyActionWithShortcut(TEXT,
        DESCRIPTION, AllStepikIcons.stepikLogo) {
    override fun actionPerformed(e: AnActionEvent?) {
        val prjManager = AppProjectManager.getInstanceEx()
        val plugins = PathManager.getPluginsPath()
        val moduleDir = FileUtil.join(plugins, "hyperskill", "hyperskill")
        val path = FileUtil.join(moduleDir, "hyperskill${ModuleFileType.DOT_DEFAULT_EXTENSION}")
        val project = prjManager.loadProject(path, "Hyperskill") ?: return
        
        if (!isStepikProject(project)) {
            StepikProjectGenerator.createProject(project)
        }
        prjManager.openProject(project)
    }
    
    override fun getActionId() = ACTION_ID
    
    override fun getShortcuts() = arrayOf(SHORTCUT)
    
    override fun update(e: AnActionEvent?) {
        val presentation = e?.presentation ?: return
        presentation.isVisible = presentation.isVisible && !isStepikProject(e.project)
    }
    
    companion object {
        private val ACTION_ID = "$pluginId.StartHyperskillAction"
        private const val SHORTCUT = "ctrl pressed ENTER"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Start Hyperskill ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Start Learn with Hyperskill"
    }
}
