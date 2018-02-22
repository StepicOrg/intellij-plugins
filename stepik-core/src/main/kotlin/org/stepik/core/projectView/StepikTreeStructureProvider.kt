package org.stepik.core.projectView

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileSystemItem
import org.stepik.core.StudyUtils
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.StudyUtils.isStepikProject
import org.stepik.core.utils.PresentationDataUtils.isVisibleDirectory
import org.stepik.core.utils.PresentationDataUtils.isVisibleFile
import org.stepik.core.utils.ProjectPsiFilesUtils.getRelativePath

abstract class StepikTreeStructureProvider : TreeStructureProvider, DumbAware {
    override fun modify(
            parent: AbstractTreeNode<*>,
            children: Collection<AbstractTreeNode<*>>,
            settings: ViewSettings): List<AbstractTreeNode<out Any>?> {
        if (!needModify(parent)) {
            return children.toList()
        }

        return children.mapNotNull { node ->
            val project = node.project ?: return@mapNotNull null
            val value = node.value
            if (isHidden(project, value)) return@mapNotNull null

            when (value) {
                is PsiDirectory -> if (isVisibleDirectory(value)) {
                    return@mapNotNull StepikDirectoryNode(project, value, settings)
                }
                is PsiFile -> if (isVisibleFile(value)) {
                    return@mapNotNull node
                }
                else -> if (shouldAdd(value)) {
                    return@mapNotNull node
                }
            }
            return@mapNotNull null
        }
    }

    private fun isHidden(project: Project, value: Any?): Boolean {
        val projectManager = getProjectManager(project) ?: return false

        if (projectManager.isAdaptive) {
            return false
        }

        if (value !is PsiFileSystemItem) {
            return false
        }

        val root = projectManager.projectRoot ?: return false
        val relativePath = getRelativePath(value)
        val node = StudyUtils.getStudyNode(root, relativePath) ?: return false
        val selected = projectManager.selected ?: return true
        val selectedPath = selected.path
        val nodePath = node.path
        return !selectedPath.startsWith(nodePath) && selected.parent !== node.parent
    }

    protected abstract fun shouldAdd(any: Any): Boolean

    private fun needModify(parent: AbstractTreeNode<*>): Boolean {
        return isStepikProject(parent.project)
    }

    override fun getData(selected: Collection<AbstractTreeNode<*>>?, dataName: String?): Any? = null
}
