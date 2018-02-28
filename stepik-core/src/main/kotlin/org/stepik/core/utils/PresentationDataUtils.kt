package org.stepik.core.utils

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.SimpleTextAttributes.STYLE_PLAIN
import com.intellij.ui.SimpleTextAttributes.STYLE_STRIKEOUT
import org.stepik.core.EduNames.SANDBOX_DIR
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.StudyUtils.getStudyNode
import org.stepik.core.StudyUtils.isStepikProject
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.icons.AllStepikIcons.ProjectTree.sandbox
import org.stepik.core.projectView.ProjectTreeMode.LESSON
import javax.swing.Icon


fun updatePresentationData(data: PresentationData, psiDirectory: PsiDirectory) {
    val project = psiDirectory.project

    if (!isStepikProject(project)) {
        return
    }

    var path = psiDirectory.relativePath
    if (path.isSandbox()) {
        setAttributes(data, SANDBOX_DIR, JBColor.BLACK, sandbox, false)
        return
    }

    path = if (path == ".") "" else path

    val node = getStudyNode(project, psiDirectory.virtualFile)
    if (path == node?.path) {
        setAttributes(project, data, node)
    } else {
        data.presentableText = psiDirectory.name
    }
}

private fun setAttributes(project: Project, data: PresentationData, item: StudyNode) {
    item.project = project
    val status = item.status
    val projectTreeMode = getProjectManager(project)?.projectTreeMode
    val itemName = when (projectTreeMode) {
        LESSON -> {
            val parent = item.parent

            if (parent != null && parent.children.size == 1) {
                parent.name
            } else {
                item.name
            }
        }
        else -> item.name
    }
    setAttributes(data, itemName, status.getColor(), item.getIcon(), item.wasDeleted)
}

private fun setAttributes(data: PresentationData, text: String, color: JBColor, icon: Icon?, deleted: Boolean) {
    var textStyle = STYLE_PLAIN
    if (deleted) {
        textStyle = textStyle or STYLE_STRIKEOUT
    }
    data.apply {
        clearText()
        addText(text, SimpleTextAttributes(textStyle, color))
        setIcon(icon)
        presentableText = text
    }
}

fun PsiDirectory.isVisibleDirectory(): Boolean {
    return this.relativePath.isVisibleDirectory()
}

fun PsiFile.isVisibleFile(): Boolean {
    return this.relativePath.isVisibleFile()
}
