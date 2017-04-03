package org.stepik.plugin.projectView;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.StudyUtils;
import org.stepik.core.courseFormat.StudyNode;

import java.util.ArrayList;
import java.util.Collection;

import static org.stepik.plugin.utils.PresentationDataUtils.isVisibleDirectory;
import static org.stepik.plugin.utils.PresentationDataUtils.isVisibleFile;
import static org.stepik.plugin.utils.ProjectPsiFilesUtils.getRelativePath;

public abstract class StepikTreeStructureProvider implements TreeStructureProvider, DumbAware {
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(
            @NotNull AbstractTreeNode parent,
            @NotNull Collection<AbstractTreeNode> children,
            ViewSettings settings) {
        if (!needModify(parent)) {
            return children;
        }
        Collection<AbstractTreeNode> nodes = new ArrayList<>();
        for (AbstractTreeNode node : children) {
            final Project project = node.getProject();
            if (project != null) {
                Object value = node.getValue();
                if (isHidden(project, value)) {
                    continue;
                }

                if (value instanceof PsiDirectory) {
                    final PsiDirectory nodeValue = (PsiDirectory) value;
                    if (isVisibleDirectory(nodeValue)) {
                        nodes.add(new StepikDirectoryNode(project, nodeValue, settings));
                    }
                } else if (value instanceof PsiFile) {
                    if (isVisibleFile((PsiFile) value)) {
                        nodes.add(node);
                    }
                } else if (shouldAdd(value)) {
                    nodes.add(node);
                }
            }
        }
        return nodes;
    }

    private boolean isHidden(@NotNull Project project, @Nullable Object value) {
        if (StepikProjectManager.isAdaptive(project)) {
            if (!(value instanceof PsiFileSystemItem)) {
                return false;
            }

            String relativePath = getRelativePath((PsiFileSystemItem) value);
            StudyNode root = StepikProjectManager.getProjectRoot(project);
            if (root == null) {
                return false;
            }

            StudyNode node = StudyUtils.getStudyNode(root, relativePath);
            if (node == null) {
                return false;
            }

            StudyNode<?, ?> selected = StepikProjectManager.getSelected(project);
            if (selected == null) {
                return true;
            }

            String selectedPath = selected.getPath();
            String nodePath = node.getPath();
            if (!selectedPath.startsWith(nodePath) && selected.getParent() != node.getParent()) {
                return true;
            }
        }
        return false;
    }

    protected abstract boolean shouldAdd(@NotNull Object object);

    private boolean needModify(@NotNull final AbstractTreeNode parent) {
        return StepikProjectManager.isStepikProject(parent.getProject());
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {
        return null;
    }
}
