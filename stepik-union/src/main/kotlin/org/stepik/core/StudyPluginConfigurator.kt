package org.stepik.core

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import javax.swing.JPanel

interface StudyPluginConfigurator {

    /**
     * Provide action group that should be placed on the tool window toolbar.
     */
    fun getActionGroup(project: Project): DefaultActionGroup

    /**
     * Provide panels, that could be added to Step tool window.
     *
     * @return Map from panel id, i.e. "Step description", to panel itself.
     */
    fun getAdditionalPanels(project: Project): Map<String, JPanel>

    fun getFileEditorManagerListener(project: Project): FileEditorManagerListener

    fun accept(project: Project): Boolean

    companion object {
        val EP_NAME = ExtensionPointName.create<StudyPluginConfigurator>(
                "org.stepik.plugin.union.studyPluginConfigurator")
    }
}
