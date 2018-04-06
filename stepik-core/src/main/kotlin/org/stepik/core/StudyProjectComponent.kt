package org.stepik.core

import com.intellij.execution.RunManager
import com.intellij.execution.RunManagerListener
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.keymap.Keymap
import com.intellij.openapi.keymap.ex.KeymapManagerEx
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.containers.hash.HashMap
import javafx.application.Platform
import org.stepik.core.actions.StudyActionWithShortcut
import org.stepik.core.common.Loggable
import org.stepik.core.metrics.Metrics
import org.stepik.core.testFramework.StepRunConfiguration
import org.stepik.core.ui.StudyToolWindowFactory.Companion.STUDY_TOOL_WINDOW
import org.stepik.core.utils.runWriteActionLater
import java.util.concurrent.Executors
import javax.swing.KeyStroke

class StudyProjectComponent private constructor(private val project: Project) :
        ProjectComponent, RunManagerListener, Loggable {
    
    private val deletedShortcuts = HashMap<Keymap, MutableList<Pair<String, String>>>()
    
    override fun projectOpened() {
        if (!isStepikProject(project)) {
            return
        }
        val projectManager = getProjectManager(project) ?: return
        
        Platform.setImplicitExit(false)
        
        registerStudyToolWindow()
        
        getApplication().runWriteActionLater {
            UISettings.instance.apply {
                hideToolStripes = false
                fireUISettingsChanged()
            }
            logger.info("register Shortcuts")
            registerShortcuts()
        }
        
        val runManager = RunManager.getInstance(project) as RunManagerImpl
        runManager.addRunManagerListener(this)
        
        
        Metrics.openProject(project)
        
        executor.execute { projectManager.updateAdaptiveSelected() }
    }
    
    fun registerStudyToolWindow() {
        if (!isStepikProject(project)) {
            return
        }
        ToolWindowManager.getInstance(project)
                .also {
                    registerToolWindows(it)
                    it.getToolWindow(STUDY_TOOL_WINDOW)
                            ?.run {
                                show(null)
                                initToolWindows(project)
                            }
                }
    }
    
    private fun registerShortcuts() {
        getStudyToolWindow(project)?.also {
            it.getActions(true)
                    .mapNotNull { it as? StudyActionWithShortcut }
                    .forEach {
                        val shortcuts = it.getShortcuts() ?: return@forEach
                        addShortcut(it.getActionId(), shortcuts)
                    }
        }
    }
    
    private fun registerToolWindows(toolWindowManager: ToolWindowManager) {
        toolWindowManager.apply {
            getToolWindow(STUDY_TOOL_WINDOW) ?: registerToolWindow(STUDY_TOOL_WINDOW,
                    true, ToolWindowAnchor.RIGHT, project, true)
        }
    }
    
    private fun addShortcut(actionIdString: String, shortcuts: Array<String>) {
        for (keymap in KeymapManagerEx.getInstanceEx().allKeymaps) {
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
        if (!isStepikProject(project)) {
            return
        }
        
        val runManager = RunManager.getInstance(project) as RunManagerImpl
        runManager.removeRunManagerListener(this)
        
        ToolWindowManager.getInstance(project)
                .getToolWindow(STUDY_TOOL_WINDOW)
                ?.contentManager?.removeAllContents(false)
        
        for (keymap in KeymapManagerEx.getInstanceEx().allKeymaps) {
            val pairs = deletedShortcuts[keymap]
            if (pairs?.isNotEmpty() == true) {
                for (actionShortcut in pairs) {
                    keymap.addShortcut(actionShortcut.first,
                            KeyboardShortcut(KeyStroke.getKeyStroke(actionShortcut.second), null))
                }
            }
        }
    }
    
    override fun runConfigurationSelected() {
        val runManager = RunManager.getInstance(project) as RunManagerImpl
        val selectedConfiguration = runManager.selectedConfiguration
        if (selectedConfiguration is StepRunConfiguration) {
            getProjectManager(project)?.selected = selectedConfiguration.stepNode
        }
    }
    
    override fun initComponent() {}
    
    override fun disposeComponent() {}
    
    override fun getComponentName(): String {
        return ProjectManager::class.java.simpleName
    }
    
    companion object {
        private val executor = Executors.newSingleThreadExecutor()
        
        fun getInstance(project: Project): StudyProjectComponent {
            return ModuleManager.getInstance(project)
                    .modules.first()
                    .getComponent(StudyProjectComponent::class.java)
        }
    }
}
