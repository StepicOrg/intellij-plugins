package org.stepik.alt.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.util.io.FileUtil
import org.stepik.alt.courseFormat.AltTree
import org.stepik.core.ProjectManager
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.actions.getShortcutText
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.stepik.StepikAuthManager
import com.intellij.openapi.project.ex.ProjectManagerEx as AppProjectManager

class StartAltAction : StudyActionWithShortcut(TEXT, DESCRIPTION, AllStepikIcons.stepikLogo) {
    override fun actionPerformed(e: AnActionEvent?) {
        val prjManager = AppProjectManager.getInstanceEx()
        val plugins = PathManager.getPluginsPath()
        val path = FileUtil.join(plugins, "alt", "alt", "default-alt-project")
        val project = prjManager.loadProject(path, "ALT") ?: return
        val projectManager = getService(project, ProjectManager::class.java) ?: return

        if (projectManager.projectRoot == null) {
            val stepikApiClient = StepikAuthManager.authAndGetStepikApiClient()
            projectManager.projectRoot = AltTree(project, stepikApiClient)
        }

        prjManager.openProject(project)
    }

    override fun getActionId() = ACTION_ID

    override fun getShortcuts() = arrayOf(SHORTCUT)

    companion object {
        private const val ACTION_ID = "Alt.StartAltAction"
        private const val SHORTCUT = "ctrl pressed ENTER"
        private val SHORTCUT_TEXT = getShortcutText(SHORTCUT)
        private val TEXT = "Start Alt ($SHORTCUT_TEXT)"
        private const val DESCRIPTION = "Start Learn with Stepik ALT"
    }
}
