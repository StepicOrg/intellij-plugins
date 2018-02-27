package org.stepik.core.utils

import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.application.ApplicationManager.getApplication
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
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath


fun navigate(project: Project, targetNode: StudyNode) {
    getApplication().invokeAndWait {
        for (file in FileEditorManager.getInstance(project).openFiles) {
            FileEditorManager.getInstance(project).closeFile(file)
        }
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
        getApplication().invokeAndWait {
            updateProjectView(project, mainFile)
        }
    }
    Metrics.navigateAction(project, targetNode)

    getProjectManager(project)?.selected = targetNode

    val runToolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.RUN)
    if (runToolWindow != null) {
        getApplication().invokeLater { runToolWindow.hide(null) }
    }
}

private fun updateProjectView(project: Project, shouldBeActive: VirtualFile) {
    var file: PsiFileSystemItem?
    if (shouldBeActive.isDirectory) {
        file = PsiManager.getInstance(project).findDirectory(shouldBeActive)
    } else {
        file = PsiManager.getInstance(project).findFile(shouldBeActive)
        FileEditorManager.getInstance(project).openFile(shouldBeActive, false)
        file = file?.parent
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
    val selectionNode = findNodeWithObject(root, file) ?: return

    val toCollapse = mutableListOf<TreePath>()
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
    tree.run {
        toCollapse.forEach { path ->
            collapsePath(path)
            fireTreeCollapsed(path)
        }
    }
}

private fun findNodeWithObject(root: TreeNode, file: PsiFileSystemItem): DefaultMutableTreeNode? {
    for (i in 0 until root.childCount) {
        val child = root.getChildAt(i)
        if (child is DefaultMutableTreeNode) {
            val userObject = child.userObject as? PsiDirectoryNode ?: continue
            if (file == userObject.value) {
                return child
            }
            return findNodeWithObject(child, file) ?: continue
        }
    }
    return null
}
