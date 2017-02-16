package org.stepik.plugin.actions.navigation;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.util.ui.tree.TreeUtil;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


abstract class StudyStepNavigationAction extends StudyActionWithShortcut {
    StudyStepNavigationAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    static void updateProjectView(@NotNull Project project, @NotNull VirtualFile shouldBeActive) {
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
            TreePath selectedPath = TreeUtil.getPathFromRoot(selectionNode);
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

    protected abstract void navigateStep(@NotNull final Project project);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        navigateStep(project);
    }

    protected abstract StudyNode getTargetStep(@Nullable final StudyNode sourceStepNode);

    @Override
    public void update(AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        presentation.setEnabled(false);

        Project project = e.getProject();
        if (!StepikProjectManager.isStepikProject(project)) {
            return;
        }

        StudyNode stepNode = StudyUtils.getSelectedNodeInTree(project);
        presentation.setEnabled(stepNode == null || getTargetStep(stepNode) != null);
    }
}
