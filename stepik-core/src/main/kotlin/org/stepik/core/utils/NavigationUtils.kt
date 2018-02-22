package org.stepik.core.utils

import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowId
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiManager
import com.intellij.util.ui.tree.TreeUtil
import com.intellij.util.ui.tree.TreeUtil.getPathFromRoot
import org.stepik.core.StudyUtils.getProjectManager
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.courseFormat.StudyNode
import org.stepik.core.metrics.Metrics
import org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath


object NavigationUtils {
    fun navigate(project: Project, targetNode: StudyNode) {
        for (file in FileEditorManager.getInstance(project).openFiles) {
            ApplicationManager.getApplication().invokeAndWait { FileEditorManager.getInstance(project).closeFile(file) }
        }

        val projectDir = project.baseDir ?: return

        var mainFile: VirtualFile?
        if (targetNode is StepNode) {
            val srcDir = getOrCreateSrcDirectory(project, targetNode, true) ?: return

            mainFile = srcDir.findChild(targetNode.currentLang.mainFileName)
            if (mainFile == null) {
                mainFile = if (srcDir.children.isNotEmpty()) srcDir else srcDir.parent
            }
        } else {
            mainFile = projectDir.findFileByRelativePath(targetNode.path)
        }

        if (mainFile != null) {
            ApplicationManager.getApplication().invokeAndWait {
                updateProjectView(project, mainFile!!)
            }
        }
        Metrics.navigateAction(project, targetNode)

        getProjectManager(project)?.selected = targetNode

        val runToolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.RUN)
        if (runToolWindow != null) {
            ApplicationManager.getApplication().invokeLater { runToolWindow.hide(null) }
        }
    }

    private fun updateProjectView(project: Project, shouldBeActive: VirtualFile) {
        var file: PsiFileSystemItem?
        if (shouldBeActive.isDirectory) {
            file = PsiManager.getInstance(project).findDirectory(shouldBeActive)
        } else {
            file = PsiManager.getInstance(project).findFile(shouldBeActive)
            FileEditorManager.getInstance(project).openFile(shouldBeActive, false)
            if (file != null) {
                file = file.parent
            }
        }

        if (file != null) {
            if (file.canNavigate()) {
                file.navigate(true)
            }
            collapseNonSelected(file)
        }
    }

    private fun collapseNonSelected(file: PsiFileSystemItem) {
        val projectView = ProjectView.getInstance(file.project) ?: return
        val projectViewPane = projectView.currentProjectViewPane ?: return
        val tree = projectViewPane.tree
        val paths = HashSet(TreeUtil.collectExpandedPaths(tree))
        val root = tree.model.root as DefaultMutableTreeNode
        val selectionNode = findNodeWithObject(root, file)

        if (selectionNode != null) {
            val toCollapse = ArrayList<TreePath>()
            val selectedPath = getPathFromRoot(selectionNode)
            for (treePath in paths) {
                if (treePath.isDescendant(selectedPath)) {
                    continue
                }
                var currPath = treePath
                var parent: TreePath? = treePath.parentPath

                while (parent != null) {
                    if (parent.isDescendant(selectedPath)) {
                        toCollapse.add(currPath)
                        break
                    }
                    currPath = parent
                    parent = parent.parentPath
                }
            }

            for (path in toCollapse) {
                tree.collapsePath(path)
                tree.fireTreeCollapsed(path)
            }
        }
    }

    private fun findNodeWithObject(
            root: TreeNode,
            file: PsiFileSystemItem): DefaultMutableTreeNode? {
        for (i in 0 until root.childCount) {
            val child = root.getChildAt(i)
            if (child is DefaultMutableTreeNode) {
                var node: DefaultMutableTreeNode? = child
                val userObject = node!!.userObject
                if (userObject is PsiDirectoryNode) {
                    val value = userObject.value
                    if (file == value) {
                        return node
                    }
                }
                node = findNodeWithObject(child, file)
                if (node != null) {
                    return node
                }
            }
        }
        return null
    }
}
