package org.stepik.plugin.utils;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.courseFormat.StepNode;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.metrics.Metrics;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.intellij.util.ui.tree.TreeUtil.getPathFromRoot;
import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;

/**
 * @author meanmail
 */
public class NavigationUtils {
    public static void navigate(@NotNull Project project, @NotNull StudyNode targetNode) {
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            FileEditorManager.getInstance(project).closeFile(file);
        }

        VirtualFile projectDir = project.getBaseDir();
        if (projectDir == null) {
            return;
        }

        VirtualFile mainFile;
        if (targetNode instanceof StepNode) {
            VirtualFile srcDir = getOrCreateSrcDirectory(project, (StepNode) targetNode, true);
            if (srcDir == null) {
                return;
            }

            StepNode stepNode = (StepNode) targetNode;

            mainFile = srcDir.findChild(stepNode.getCurrentLang().getMainFileName());
            if (mainFile == null) {
                mainFile = srcDir.getChildren().length > 0 ? srcDir : srcDir.getParent();
            }
        } else {
            mainFile = projectDir.findFileByRelativePath(targetNode.getPath());
        }

        if (mainFile != null) {
            updateProjectView(project, mainFile);
        }
        Metrics.navigateAction(project, targetNode, SUCCESSFUL);
        StepikProjectManager.setSelected(project, targetNode);

        ToolWindow runToolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.RUN);
        if (runToolWindow != null) {
            runToolWindow.hide(null);
        }
    }

    private static void updateProjectView(@NotNull Project project, @NotNull VirtualFile shouldBeActive) {
        PsiFileSystemItem file;
        if (shouldBeActive.isDirectory()) {
            file = PsiManager.getInstance(project).findDirectory(shouldBeActive);
        } else {
            file = PsiManager.getInstance(project).findFile(shouldBeActive);
            FileEditorManager.getInstance(project).openFile(shouldBeActive, false);
            if (file != null) {
                file = file.getParent();
            }
        }

        if (file != null) {
            if (file.canNavigate()) {
                file.navigate(true);
            }
            collapseNonSelected(file);
        }
    }

    private static void collapseNonSelected(@NotNull PsiFileSystemItem file) {
        JTree tree = ProjectView.getInstance(file.getProject()).getCurrentProjectViewPane().getTree();
        Set<TreePath> paths = new HashSet<>(TreeUtil.collectExpandedPaths(tree));
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        DefaultMutableTreeNode selectionNode = findNodeWithObject(root, file);

        if (selectionNode != null) {
            List<TreePath> toCollapse = new ArrayList<>();
            TreePath selectedPath = getPathFromRoot(selectionNode);
            for (TreePath treePath : paths) {
                if (treePath.isDescendant(selectedPath)) {
                    continue;
                }
                TreePath currPath = treePath;
                TreePath parent = treePath.getParentPath();

                while (parent != null) {
                    if (parent.isDescendant(selectedPath)) {
                        toCollapse.add(currPath);
                        break;
                    }
                    currPath = parent;
                    parent = parent.getParentPath();
                }
            }

            for (TreePath path : toCollapse) {
                tree.collapsePath(path);
                tree.fireTreeCollapsed(path);
            }
        }
    }

    @Nullable
    private static DefaultMutableTreeNode findNodeWithObject(
            @NotNull TreeNode root,
            PsiFileSystemItem file) {
        for (int i = 0; i < root.getChildCount(); i++) {
            TreeNode child = root.getChildAt(i);
            if (child instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) child;
                Object userObject = node.getUserObject();
                if (userObject instanceof PsiDirectoryNode) {
                    PsiDirectoryNode directoryNode = (PsiDirectoryNode) userObject;
                    PsiDirectory value = directoryNode.getValue();
                    if (file.equals(value)) {
                        return node;
                    }
                }
                node = findNodeWithObject(child, file);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }
}
