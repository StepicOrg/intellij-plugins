package org.stepik.plugin.projectView;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import static org.stepik.plugin.utils.PresentationUtils.isSourceFile;
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
                        AbstractTreeNode newNode = new StepikDirectoryNode(project, nodeValue, settings);
                        nodes.add(newNode);
                    }
                } else if (parent instanceof StepikDirectoryNode && node instanceof PsiFileNode) {
                    final PsiFile nodeValue = ((PsiFileNode) node).getValue();
                    if (isVisibleFile(nodeValue)) {
                        if (isSourceFile(nodeValue)) {
                            nodes.add(node);
                            continue;
                        }
                        final VirtualFile virtualFile = nodeValue.getVirtualFile();
                        if (virtualFile == null) {
                            continue;
                        }
                        final TaskFile taskFile = StudyUtils.getTaskFile(project, virtualFile);
                        if (taskFile != null) {
                            nodes.add(node);
                        }
                        final String parentName = parent.getName();
                        if (parentName != null) {
                            if (parentName.equals(EduNames.SANDBOX_DIR)) {
                                nodes.add(node);
                            }
                            if (parentName.startsWith(EduNames.TASK)) {
                                addNonInvisibleFiles(nodes, node, project, virtualFile);
                            }
                        }
                    }
                }
            }
        }
        return nodes;
    }

    private static void addNonInvisibleFiles(
            @NotNull final Collection<AbstractTreeNode> nodes,
            @NotNull final AbstractTreeNode node,
            @NotNull final Project project,
            @NotNull final VirtualFile virtualFile) {
        if (!StudyTaskManager.getInstance(project).isInvisibleFile(virtualFile.getPath())) {
            String fileName = virtualFile.getName();
            if (!fileName.contains(EduNames.WINDOW_POSTFIX) && !fileName.contains(EduNames.WINDOWS_POSTFIX)
                    && !StudyUtils.isTestsFile(project, fileName) && !StudyUtils.isTaskDescriptionFile(fileName)) {
                nodes.add(node);
            }
        }
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
