package org.stepik.plugin.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.utils.PresentationDataUtils;

import java.util.Map;

/**
 * @author meanmail
 */
class StepikDirectoryNode extends PsiDirectoryNode {

    StepikDirectoryNode(
            Project project,
            PsiDirectory value,
            ViewSettings viewSettings) {
        super(project, value, viewSettings);
    }

    @Override
    protected void updateImpl(@NotNull PresentationData data) {
        PresentationDataUtils.updatePresentationData(data, getValue());
    }

    @Override
    public int getTypeSortWeight(boolean sortByType) {
        String name = getValue().getName();
        if (name.startsWith(EduNames.SECTION)) {
            return EduUtils.getIndex(name, EduNames.SECTION);
        }
        if (name.startsWith(EduNames.LESSON)) {
            return EduUtils.getIndex(name, EduNames.LESSON);
        }
        if (name.startsWith(EduNames.TASK)) {
            return EduUtils.getIndex(name, EduNames.TASK);
        }

        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    @Override
    public void navigate(boolean requestFocus) {
        final PsiDirectory value = getValue();
        final String myValueName = value.getName();
        if (myValueName.contains(EduNames.TASK)) {
            TaskFile taskFile = null;
            VirtualFile virtualFile = null;
            for (PsiElement child : value.getChildren()) {
                PsiFile containingFile = child.getContainingFile();
                //TODO fix. Step don't open by double click
                if (containingFile == null)
                    break;
                VirtualFile childFile = containingFile.getVirtualFile();
                taskFile = StudyUtils.getTaskFile(myProject, childFile);
                if (taskFile != null) {
                    virtualFile = childFile;
                    break;
                }
            }
            if (taskFile != null) {
                VirtualFile taskDir = virtualFile.getParent();
                Task task = taskFile.getTask();
                for (VirtualFile openFile : FileEditorManager.getInstance(myProject).getOpenFiles()) {
                    FileEditorManager.getInstance(myProject).closeFile(openFile);
                }
                VirtualFile child = null;
                Map<String, TaskFile> taskFiles = task.getTaskFiles();
                for (Map.Entry<String, TaskFile> entry : taskFiles.entrySet()) {
                    VirtualFile file = taskDir.findChild(entry.getKey());
                    if (file != null) {
                        FileEditorManager.getInstance(myProject).openFile(file, true);
                    }
                    child = file;
                }
                if (child != null) {
                    ProjectView.getInstance(myProject).select(child, child, false);
                    FileEditorManager.getInstance(myProject).openFile(child, true);
                } else {
                    VirtualFile[] children = taskDir.getChildren();
                    if (children.length > 0) {
                        ProjectView.getInstance(myProject).select(children[0], children[0], false);
                    }
                }
            }
        }
    }

    @Override
    protected boolean hasProblemFileBeneath() {
        return false;
    }

    @Override
    public String getNavigateActionText(boolean focusEditor) {
        return null;
    }
}
