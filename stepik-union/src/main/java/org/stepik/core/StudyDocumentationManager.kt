package org.stepik.core

import com.intellij.codeInsight.TargetElementUtil
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowType
import com.intellij.openapi.wm.WindowManager
import java.awt.Rectangle

class StudyDocumentationManager(project: Project, manager: ActionManager, targetElementUtil: TargetElementUtil) :
        DocumentationManager(project, manager, targetElementUtil) {

    override fun setToolwindowDefaultState() {
        val rectangle = WindowManager.getInstance().getIdeFrame(myProject).suggestChildFrameBounds()
        myToolWindow.setDefaultState(ToolWindowAnchor.RIGHT,
                ToolWindowType.DOCKED,
                Rectangle(rectangle.width / 2, rectangle.height))
    }
}
