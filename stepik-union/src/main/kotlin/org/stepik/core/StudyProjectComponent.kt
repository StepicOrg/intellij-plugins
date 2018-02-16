package org.stepik.core

import com.intellij.ide.ui.UISettings
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.keymap.Keymap
import com.intellij.openapi.keymap.ex.KeymapManagerEx
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.DumbAwareRunnable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.containers.hash.HashMap
import javafx.application.Platform
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.metrics.Metrics
import org.stepik.core.metrics.MetricsStatus.SUCCESSFUL
import org.stepik.core.ui.StudyToolWindowFactory
import java.util.concurrent.Executors
import javax.swing.KeyStroke

class StudyProjectComponent private constructor(private val project: Project) : ProjectComponent {
    private val deletedShortcuts = HashMap<Keymap, MutableList<Pair<String, String>>>()

    override fun projectOpened() {
        if (!StepikProjectManager.isStepikProject(project)) {
            return
        }

        Platform.setImplicitExit(false)

        registerStudyToolWindow()
        ApplicationManager.getApplication().invokeLater(
                {
                    ApplicationManager.getApplication()
                            .runWriteAction({
                                val uiSettings = UISettings.instance
                                uiSettings.hideToolStripes = false
                                uiSettings.fireUISettingsChanged()
                                logger.info("register Shortcuts")
                                registerShortcuts()
                            } as DumbAwareRunnable)
                } as DumbAwareRunnable)
        Metrics.openProject(project, SUCCESSFUL)

        executor.execute { StepikProjectManager.updateAdaptiveSelected(project) }
    }

    fun registerStudyToolWindow() {
        if (!StepikProjectManager.isStepikProject(project)) {
            return
        }
        val toolWindowManager = ToolWindowManager.getInstance(project)
        registerToolWindows(toolWindowManager)
        val studyToolWindow = toolWindowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW)
        if (studyToolWindow != null) {
            studyToolWindow.show(null)
            StudyUtils.initToolWindows(project)
        }
    }

    private fun registerShortcuts() {
        val window = StudyUtils.getStudyToolWindow(project)
        if (window != null) {
            val actionsOnToolbar = window.getActions(true)
            actionsOnToolbar.stream()
                    .filter { action -> action is StudyActionWithShortcut }
                    .map { action -> action as StudyActionWithShortcut }
                    .forEach { action ->
                        val id = action.getActionId()
                        val shortcuts = action.getShortcuts()
                        if (shortcuts != null) {
                            addShortcut(id, shortcuts)
                        }
                    }
        }
    }

    private fun registerToolWindows(toolWindowManager: ToolWindowManager) {
        val toolWindow = toolWindowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW)
        if (toolWindow == null) {
            toolWindowManager.registerToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW,
                    true,
                    ToolWindowAnchor.RIGHT,
                    project,
                    true)
        }
    }

    private fun addShortcut(actionIdString: String, shortcuts: Array<String>) {
        val keymapManager = KeymapManagerEx.getInstanceEx()
        for (keymap in keymapManager.allKeymaps) {
            val pairs = deletedShortcuts.computeIfAbsent(keymap) { mutableListOf() }
            for (shortcutString in shortcuts) {
                val studyActionShortcut = KeyboardShortcut(KeyStroke.getKeyStroke(shortcutString), null)
                val actionsIds = keymap.getActionIds(studyActionShortcut)
                for (actionId in actionsIds) {
                    pairs.add(Pair.create(actionId, shortcutString))
                    keymap.removeShortcut(actionId, studyActionShortcut)
                }
                keymap.addShortcut(actionIdString, studyActionShortcut)
            }
        }
    }

    override fun projectClosed() {
        if (!StepikProjectManager.isStepikProject(project)) {
            return
        }

        val toolWindow = ToolWindowManager.getInstance(project)
                .getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW)
        toolWindow?.contentManager?.removeAllContents(false)
        val keymapManager = KeymapManagerEx.getInstanceEx()
        for (keymap in keymapManager.allKeymaps) {
            val pairs = deletedShortcuts[keymap]
            if (pairs != null && !pairs.isEmpty()) {
                for (actionShortcut in pairs) {
                    keymap.addShortcut(actionShortcut.first,
                            KeyboardShortcut(KeyStroke.getKeyStroke(actionShortcut.second), null))
                }
            }
        }
    }

    override fun initComponent() {}

    override fun disposeComponent() {}

    override fun getComponentName(): String {
        return StepikProjectManager::class.java.simpleName
    }

    companion object {
        private val logger = Logger.getInstance(StudyProjectComponent::class.java.name)
        private val executor = Executors.newSingleThreadExecutor()

        fun getInstance(project: Project): StudyProjectComponent {
            val module = ModuleManager.getInstance(project).modules[0]
            return module.getComponent(StudyProjectComponent::class.java)
        }
    }
}
