package org.stepik.plugin.utils

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleTextAttributes
import org.stepik.core.StepikProjectManager
import org.stepik.core.StudyUtils
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.utils.PresentationUtils
import org.stepik.core.utils.ProjectFilesUtils
import org.stepik.plugin.utils.ProjectPsiFilesUtils.getRelativePath
import javax.swing.Icon


object PresentationDataUtils {

    fun updatePresentationData(data: PresentationData, psiDirectory: PsiDirectory) {
        val project = psiDirectory.project
        val valueName = psiDirectory.name

        StepikProjectManager.getProjectRoot(project) ?: return

        var path = getRelativePath(psiDirectory)
        if (ProjectFilesUtils.isSandbox(path)) {
            setAttributes(data, EduNames.SANDBOX_DIR, JBColor.BLACK, AllStepikIcons.ProjectTree.sandbox, false)
            return
        }

        path = if ("." == path) "" else path

        val node = StudyUtils.getStudyNode(project, psiDirectory.virtualFile)
        if (node != null && path == node.path) {
            setAttributes(project, data, node)
        } else {
            data.presentableText = valueName
        }
    }

    private fun setAttributes(
            project: Project,
            data: PresentationData,
            item: StudyNode<*, *>) {
        val text = item.name
        item.setProject(project)
        val status = item.status
        val color = PresentationUtils.getColor(status)
        val icon = PresentationUtils.getIcon(item, status)
        setAttributes(data, text, color, icon, item.wasDeleted)
    }

    private fun setAttributes(
            data: PresentationData,
            text: String,
            color: JBColor,
            icon: Icon?,
            deleted: Boolean) {
        data.clearText()
        var textStyle = SimpleTextAttributes.STYLE_PLAIN
        if (deleted) {
            textStyle = textStyle or SimpleTextAttributes.STYLE_STRIKEOUT
        }
        data.addText(text, SimpleTextAttributes(textStyle, color))
        data.setIcon(icon)
        data.presentableText = text
    }

    fun isVisibleDirectory(psiDirectory: PsiDirectory): Boolean {
        val relPath = getRelativePath(psiDirectory)
        return PresentationUtils.isVisibleDirectory(relPath)
    }

    fun isVisibleFile(psiFile: PsiFile): Boolean {
        val relPath = getRelativePath(psiFile)
        return PresentationUtils.isVisibleFile(relPath)
    }
}
