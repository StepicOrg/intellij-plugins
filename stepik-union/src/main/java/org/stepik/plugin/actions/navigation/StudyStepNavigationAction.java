package org.stepik.plugin.actions.navigation;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.tree.TreeUtil;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


abstract class StudyStepNavigationAction extends StudyActionWithShortcut {
    StudyStepNavigationAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    static void updateProjectView(@NotNull Project project, VirtualFile shouldBeActive) {
        JTree tree = ProjectView.getInstance(project).getCurrentProjectViewPane().getTree();
        if (shouldBeActive != null) {
            ProjectView.getInstance(project).selectCB(shouldBeActive, shouldBeActive, false).doWhenDone(() -> {
                List<TreePath> paths = TreeUtil.collectExpandedPaths(tree);
                List<TreePath> toCollapse = new ArrayList<>();
                TreePath selectedPath = tree.getSelectionPath();
                for (TreePath treePath : paths) {
                    if (treePath.isDescendant(selectedPath)) {
                        continue;
                    }
                    TreePath currPath = treePath;
                    TreePath parent = treePath.getParentPath();

                    while (parent != null) {
                        if (parent.isDescendant(selectedPath)) {
                            if (!toCollapse.contains(currPath)) {
                                toCollapse.add(currPath);
                            }
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
            });
            FileEditorManager.getInstance(project).openFile(shouldBeActive, true);
        }
    }

    @Nullable
    private static VirtualFile getFirstStepFile(@NotNull final VirtualFile stepDir, @NotNull final Project project) {
        for (VirtualFile virtualFile : stepDir.getChildren()) {
            if (StudyUtils.getStepFile(project, virtualFile) != null) {
                return virtualFile;
            }
        }
        return null;
    }

    protected abstract void navigateStep(@NotNull final Project project);

    @Nullable
    VirtualFile getFileToActivate(
            @NotNull Project project,
            @NotNull Map<String, StepFile> nextStepFiles,
            @NotNull VirtualFile stepDir) {
        VirtualFile shouldBeActive = null;
        VirtualFile srcDir = stepDir.findChild(EduNames.SRC);

        for (Map.Entry<String, StepFile> entry : nextStepFiles.entrySet()) {
            String name = entry.getKey();

            VirtualFile vf = srcDir != null ? srcDir.findChild(name) : null;
            if (vf != null) {
                if (shouldBeActive != null) {
                    FileEditorManager.getInstance(project).openFile(vf, true);
                } else {
                    shouldBeActive = vf;
                }
            }
        }
        return shouldBeActive != null ? shouldBeActive : getFirstStepFile(stepDir, project);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        navigateStep(project);
    }

    protected abstract StepNode getTargetStep(@NotNull final StepNode sourceStepNode);

    @Override
    public void update(AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        presentation.setEnabled(false);

        Project project = e.getProject();
        if (!StepikProjectManager.isStepikProject(project)) {
            return;
        }

        StepNode stepNode = StudyUtils.getSelectedStep(project);
        presentation.setEnabled(stepNode == null || getTargetStep(stepNode) != null);
    }
}
