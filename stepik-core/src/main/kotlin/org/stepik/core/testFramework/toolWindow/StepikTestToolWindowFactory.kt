package org.stepik.core.testFramework.toolWindow

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import org.stepik.core.ProjectManager

class StepikTestToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val projectManager = ServiceManager.getService(project, ProjectManager::class.java)
        val currentTask = projectManager.selected
        if (currentTask != null) {
            val consoleView = ConsoleViewImpl(project, true)
            toolWindow.isToHideOnEmptyContent = true

            val contentManager = toolWindow.contentManager
            val content = contentManager.factory.createContent(consoleView.component, null, false)
            contentManager.addContent(content)
            val editor = consoleView.editor
            if (editor is EditorEx) {
                editor.isRendererMode = true
            }

            val topic = FileEditorManagerListener.FILE_EDITOR_MANAGER
            val handler = StepikTestFileEditorManagerListener(toolWindow)
            project.messageBus.connect().subscribe(topic, handler)
        }
    }

    class StepikTestFileEditorManagerListener(val toolWindow: ToolWindow) : FileEditorManagerListener {
        override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        }

        override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
            toolWindow.setAvailable(false, {})
        }

        override fun selectionChanged(event: FileEditorManagerEvent) {
            toolWindow.setAvailable(false, {})
        }
    }

    companion object {
        const val ID = "Stepik Test"
    }
}
