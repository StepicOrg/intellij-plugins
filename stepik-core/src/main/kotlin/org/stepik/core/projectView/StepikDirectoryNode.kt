package org.stepik.core.projectView

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import org.stepik.core.ProjectManager
import org.stepik.core.StudyUtils
import org.stepik.core.utils.PresentationDataUtils
import org.stepik.core.utils.ProjectPsiFilesUtils.getRelativePath


internal class StepikDirectoryNode(
        project: Project,
        value: PsiDirectory,
        viewSettings: ViewSettings) : PsiDirectoryNode(project, value, viewSettings) {

    override fun updateImpl(data: PresentationData) {
        PresentationDataUtils.updatePresentationData(data, value)
    }

    override fun getTypeSortWeight(sortByType: Boolean): Int {
        val node = StudyUtils.getStudyNode(myProject, value.virtualFile)
        return if (node?.path == getRelativePath(value)) node.position else Int.MAX_VALUE
    }

    override fun canNavigate() = true

    override fun canNavigateToSource() = true

    override fun hasProblemFileBeneath() = false

    override fun getNavigateActionText(focusEditor: Boolean): String? = null

    override fun navigate(requestFocus: Boolean) {
        val virtualFile = virtualFile
        if (virtualFile != null) {
            val studyNode = StudyUtils.getStudyNode(myProject, virtualFile)
            val projectManager = getService(myProject, ProjectManager::class.java)
            projectManager.selected = studyNode
        }
    }
}