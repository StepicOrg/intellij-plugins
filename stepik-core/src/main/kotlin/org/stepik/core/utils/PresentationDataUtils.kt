package org.stepik.core.utils

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleTextAttributes
import org.stepik.core.ProjectManager
import org.stepik.core.StudyUtils.getStudyNode
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.icons.AllStepikIcons
import org.stepik.core.utils.PresentationUtils.getColor
import org.stepik.core.utils.PresentationUtils.getIcon
import org.stepik.core.utils.ProjectFilesUtils.isSandbox
import org.stepik.core.utils.ProjectPsiFilesUtils.getRelativePath
import javax.swing.Icon


object PresentationDataUtils {

    fun updatePresentationData(data: PresentationData, psiDirectory: PsiDirectory) {
        val project = psiDirectory.project

        val projectManager = getService(project, ProjectManager::class.java)
        projectManager.projectRoot ?: return

        var path = getRelativePath(psiDirectory)
        if (isSandbox(path)) {
            setAttributes(data, EduNames.SANDBOX_DIR, JBColor.BLACK, AllStepikIcons.ProjectTree.sandbox, false)
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

    private fun setAttributes(
            project: Project,
            data: PresentationData,
            item: StudyNode<*, *>) {
        item.setProject(project)
        val status = item.status
        val color = getColor(status)
        val icon = getIcon(item, status)
        setAttributes(data, item.name, color, icon, item.wasDeleted)
    }

    private fun setAttributes(
            data: PresentationData,
            text: String,
            color: JBColor,
            icon: Icon?,
            deleted: Boolean) {
        var textStyle = SimpleTextAttributes.STYLE_PLAIN
        if (deleted) {
            textStyle = textStyle or SimpleTextAttributes.STYLE_STRIKEOUT
        }
        data.apply {
            clearText()
            addText(text, SimpleTextAttributes(textStyle, color))
            setIcon(icon)
            presentableText = text
        }
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
