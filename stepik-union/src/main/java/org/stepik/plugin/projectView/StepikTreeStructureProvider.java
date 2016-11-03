package org.stepik.plugin.projectView;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import static org.stepik.plugin.utils.PresentationUtils.isVisibleDirectory;
import static org.stepik.plugin.utils.PresentationUtils.isVisibleFile;

public class StepikTreeStructureProvider implements TreeStructureProvider, DumbAware {
    private static final Logger logger = Logger.getInstance(StepikTreeStructureProvider.class);

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
                if (node.getValue() instanceof PsiDirectory) {
                    final PsiDirectory nodeValue = (PsiDirectory) node.getValue();
                    if (isVisibleDirectory(nodeValue)) {
                        nodes.add(new StepikDirectoryNode(project, nodeValue, settings));
                    }
                } else if (node instanceof PsiFileNode) {
                    if (isVisibleFile(((PsiFileNode) node).getValue())) {
                        nodes.add(node);
                    }
                }
            }
        }
        return nodes;
    }

    private boolean needModify(@NotNull final AbstractTreeNode parent) {
        final Project project = parent.getProject();
        if (project == null) {
            return false;
        }
        final StudyTaskManager studyTaskManager = StudyTaskManager.getInstance(project);
        Course course = studyTaskManager.getCourse();
        return course != null && EduNames.STEPIK_CODE.equals(course.getCourseMode());
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {
        return null;
    }
}
