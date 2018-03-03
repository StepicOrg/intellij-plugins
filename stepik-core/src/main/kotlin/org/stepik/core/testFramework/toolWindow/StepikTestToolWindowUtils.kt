package org.stepik.core.testFramework.toolWindow

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.ConsoleViewContentType.NORMAL_OUTPUT
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import org.stepik.core.testFramework.toolWindow.StepikTestToolWindowFactory.Companion.ID

fun showTestResultsToolWindow(project: Project, title: String = ID): StepikTestResultToolWindow {
    val toolWindowManager = ToolWindowManager.getInstance(project)
    var window: ToolWindow? = toolWindowManager.getToolWindow(ID)
    if (window == null) {
        toolWindowManager.registerToolWindow(ID, true, ToolWindowAnchor.BOTTOM)
        window = toolWindowManager.getToolWindow(ID)
        StepikTestToolWindowFactory().createToolWindowContent(project, window)
    }

    for (content in window!!.contentManager.contents) {
        val component = content.component
        if (component is ConsoleViewImpl) {
            window.apply {
                setAvailable(true) { }
                this.title = title
                show { }
            }
            return StepikTestResultToolWindow(component)
        }
    }
    return StepikTestResultToolWindowStub(project)
}

open class StepikTestResultToolWindow(val component: ConsoleViewImpl) {

    fun print(message: String, contentType: ConsoleViewContentType = NORMAL_OUTPUT) {
        component.print(message, contentType)
    }

    fun println(message: String = "", contentType: ConsoleViewContentType = NORMAL_OUTPUT) {
        component.print("$message\n", contentType)
    }

    fun clear() {
        component.clear()
    }

    fun clearLastLine() {
        getApplication().invokeAndWait {
            component.flushDeferredText()
            val document = component.editor.document

            val line = document.lineCount - 1
            if (line < 0) {
                return@invokeAndWait
            }

            document.run {
                deleteString(getLineStartOffset(line), getLineEndOffset(line))
            }
        }
    }
}

class StepikTestResultToolWindowStub(project: Project) :
        StepikTestResultToolWindow(ConsoleViewImpl(project, true))
