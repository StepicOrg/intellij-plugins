package org.stepik.alt.actions

import com.intellij.ide.highlighter.ModuleFileType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.util.io.FileUtil
import org.stepik.alt.projectWizard.StepikProjectGenerator
import org.stepik.core.StudyUtils.isStepikProject
import org.stepik.core.StudyUtils.pluginId
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.actions.getShortcutText
import org.stepik.core.icons.AllStepikIcons
import com.intellij.openapi.project.ex.ProjectManagerEx as AppProjectManager

class StartAltAction : StudyActionWithShortcut(TEXT, DESCRIPTION, AllStepikIcons.stepikLogo) {
    override fun actionPerformed(e: AnActionEvent?) {
        val prjManager = AppProjectManager.getInstanceEx()
        val plugins = PathManager.getPluginsPath()
        val moduleDir = FileUtil.join(plugins, "alt", "alt")
        val path = FileUtil.join(moduleDir, "alt${ModuleFileType.DOT_DEFAULT_EXTENSION}")
        val project = prjManager.loadProject(path, "ALT") ?: return

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
        private val ACTION_ID = "$pluginId.StartAltAction"
        private const val SHORTCUT = "ctrl pressed ENTER"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Start Alt ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Start Learn with Stepik ALT"
    }
}
