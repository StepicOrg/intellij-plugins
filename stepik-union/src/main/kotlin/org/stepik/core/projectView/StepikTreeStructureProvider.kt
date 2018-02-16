package org.stepik.core.projectView

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileSystemItem
import org.stepik.core.StepikProjectManager
import org.stepik.core.StudyUtils
import org.stepik.plugin.utils.PresentationDataUtils.isVisibleDirectory
import org.stepik.plugin.utils.PresentationDataUtils.isVisibleFile
import org.stepik.plugin.utils.ProjectPsiFilesUtils.getRelativePath
import java.util.*

abstract class StepikTreeStructureProvider : TreeStructureProvider, DumbAware {
    override fun modify(
            parent: AbstractTreeNode<*>,
            children: Collection<AbstractTreeNode<*>>,
            settings: ViewSettings): Collection<AbstractTreeNode<*>> {
        if (!needModify(parent)) {
            return children
        }
        val nodes = ArrayList<AbstractTreeNode<*>>()
        for (node in children) {
            val project = node.project
            if (project != null) {
                val value = node.value
                if (isHidden(project, value)) {
                    continue
                }

                if (value is PsiDirectory) {
                    if (isVisibleDirectory(value)) {
                        nodes.add(StepikDirectoryNode(project, value, settings))
                    }
                } else if (value is PsiFile) {
                    if (isVisibleFile(value)) {
                        nodes.add(node)
                    }
                } else if (shouldAdd(value)) {
                    nodes.add(node)
                }
            }
        }
        return nodes
    }

    private fun isHidden(project: Project, value: Any?): Boolean {
        if (StepikProjectManager.isAdaptive(project)) {
            if (value !is PsiFileSystemItem) {
                return false
            }

            val relativePath = getRelativePath((value as PsiFileSystemItem?)!!)
            val root = StepikProjectManager.getProjectRoot(project) ?: return false

            val node = StudyUtils.getStudyNode(root, relativePath) ?: return false

            val selected = StepikProjectManager.getSelected(project) ?: return true

            val selectedPath = selected.path
            val nodePath = node.path
            if (!selectedPath.startsWith(nodePath) && selected.parent !== node.parent) {
                return true
            }
        }
        return false
    }

    protected abstract fun shouldAdd(any: Any): Boolean

    private fun needModify(parent: AbstractTreeNode<*>): Boolean {
        return StepikProjectManager.isStepikProject(parent.project)
    }

    override fun getData(selected: Collection<AbstractTreeNode<*>>?, dataName: String?): Any? = null
}
