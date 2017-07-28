package org.stepik.core.testFramework.toolWindow

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager

class StepikTestToolWindowUtils {
    companion object {
        fun showTestResultsToolWindow(project: Project,
                                      title: String = StepikTestToolWindowFactory.ID): StepikTestResultToolWindow {
            val toolWindowManager = ToolWindowManager.getInstance(project)
            var window: ToolWindow? = toolWindowManager.getToolWindow(StepikTestToolWindowFactory.ID)
            if (window == null) {
                toolWindowManager.registerToolWindow(StepikTestToolWindowFactory.ID, true, ToolWindowAnchor.BOTTOM)
                window = toolWindowManager.getToolWindow(StepikTestToolWindowFactory.ID)
                StepikTestToolWindowFactory().createToolWindowContent(project, window)
            }

            val contents = window!!.contentManager.contents
            for (content in contents) {
                val component = content.component
                if (component is ConsoleViewImpl) {
                    window.setAvailable(true) { }
                    window.title = title
                    window.show { }
                    return StepikTestResultToolWindow(component)
                }
            }
            return StepikTestResultToolWindowStub(project)
        }
    }
}

open class StepikTestResultToolWindow(val component: ConsoleViewImpl) {

    fun print(message: String, contentType: ConsoleViewContentType = ConsoleViewContentType.NORMAL_OUTPUT) {
        component.print(message, contentType)
    }

    fun println(message: String = "", contentType: ConsoleViewContentType = ConsoleViewContentType.NORMAL_OUTPUT) {
        component.print("$message\n", contentType)
    }

    fun clear() {
        component.clear()
    }

    fun clearLastLine() {
        ApplicationManager.getApplication().invokeAndWait {
            component.flushDeferredText()
            val document = component.editor.document

            val line = document.lineCount - 1
            if (line < 0) {
                return@invokeAndWait
            }
            val start = document.getLineStartOffset(line)
            val end = document.getLineEndOffset(line)
            document.deleteString(start, end)
        }
    }
}

class StepikTestResultToolWindowStub(project: Project) : StepikTestResultToolWindow(ConsoleViewImpl(project, true))
