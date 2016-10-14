package org.stepik.plugin;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import core.com.jetbrains.tmp.learning.StudyTaskManager;
import core.com.jetbrains.tmp.learning.StudyUtils;
import core.com.jetbrains.tmp.learning.core.EduNames;
import core.com.jetbrains.tmp.learning.courseFormat.Course;
import core.com.jetbrains.tmp.learning.courseFormat.TaskFile;
import core.com.jetbrains.tmp.learning.projectView.StudyDirectoryNode;
import com.siyeh.ig.psiutils.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class StepikTreeStructureProvider implements TreeStructureProvider, DumbAware {
  private static final Logger LOG = Logger.getInstance(StepikTreeStructureProvider.class);
  @NotNull
  @Override
  public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
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
          final PsiDirectory nodeValue = (PsiDirectory)node.getValue();
          final String name = nodeValue.getName();
          if (!name.contains(EduNames.USER_TESTS) && !name.startsWith(".") && !"lib".equals(name) && !name.equals("hide")) {
            AbstractTreeNode newNode = createStudyDirectoryNode(settings, project, nodeValue);
            nodes.add(newNode);
          }
        }
        else {
          if (parent instanceof StudyDirectoryNode && node instanceof PsiFileNode) {
            final PsiFileNode psiFileNode = (PsiFileNode)node;
            final VirtualFile virtualFile = psiFileNode.getVirtualFile();
            if (virtualFile == null) {
//              return nodes;
              continue;
            }
            if (virtualFile.getName().endsWith(".iml")) continue;
            if (virtualFile.getName().endsWith(".java") || virtualFile.getName().endsWith(".py")){
                nodes.add(node);
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

  @NotNull
  protected AbstractTreeNode createStudyDirectoryNode(ViewSettings settings, Project project, PsiDirectory nodeValue) {
    return new StudyDirectoryNode(project, nodeValue, settings);
  }

  private static void addNonInvisibleFiles(@NotNull final Collection<AbstractTreeNode> nodes,
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

  protected boolean needModify(@NotNull final AbstractTreeNode parent) {
    final Project project = parent.getProject();
    if (project == null) {
      return false;
    }
    final StudyTaskManager studyTaskManager = StudyTaskManager.getInstance(project);
    Course course = studyTaskManager.getCourse();
    if (course == null) {
      return false;
    }
    return EduNames.STEPIK_CODE.equals(course.getCourseMode());
  }

  @Nullable
  @Override
  public Object getData(Collection<AbstractTreeNode> selected, String dataName) {
    return null;
  }
}
