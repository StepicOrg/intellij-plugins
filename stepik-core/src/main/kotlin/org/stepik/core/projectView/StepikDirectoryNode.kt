package org.stepik.core.projectView

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import org.stepik.core.getProjectManager
import org.stepik.core.getStudyNode
import org.stepik.core.utils.relativePath
import org.stepik.core.utils.updatePresentationData
import kotlin.Int.Companion.MAX_VALUE

internal class StepikDirectoryNode(
        project: Project,
        value: PsiDirectory,
        viewSettings: ViewSettings) : PsiDirectoryNode(project, value, viewSettings) {
    
    override fun updateImpl(data: PresentationData) {
        updatePresentationData(data, value)
    }
    
    override fun getTypeSortWeight(sortByType: Boolean): Int {
        val node = getStudyNode(myProject, value.virtualFile)
        return if (node?.path == value.relativePath) node.position else MAX_VALUE
    }
    
    override fun canNavigate() = true
    
    override fun canNavigateToSource() = true
    
    override fun hasProblemFileBeneath() = false
    
    override fun getNavigateActionText(focusEditor: Boolean): String? = null
    
    override fun navigate(requestFocus: Boolean) {
        val virtualFile = virtualFile
        if (virtualFile != null) {
            val studyNode = getStudyNode(myProject, virtualFile)
            getProjectManager(myProject)?.selected = studyNode
        }
    }
}
