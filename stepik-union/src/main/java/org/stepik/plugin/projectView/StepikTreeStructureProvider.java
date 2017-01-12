package org.stepik.plugin.projectView;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import static org.stepik.plugin.utils.PresentationDataUtils.isVisibleDirectory;
import static org.stepik.plugin.utils.PresentationDataUtils.isVisibleFile;

abstract class StepikTreeStructureProvider implements TreeStructureProvider, DumbAware {
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

    protected abstract boolean shouldAdd(@NotNull Object object);

    private boolean needModify(@NotNull final AbstractTreeNode parent) {
        final Project project = parent.getProject();
        if (project == null) {
            return false;
        }
        final StepikProjectManager stepikProjectManager = StepikProjectManager.getInstance(project);
        CourseNode courseNode = stepikProjectManager.getCourseNode();
        return courseNode != null;
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {
        return null;
    }
}
