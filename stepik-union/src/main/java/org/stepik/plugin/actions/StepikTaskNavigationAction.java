package org.stepik.plugin.actions;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.jetbrains.tmp.learning.StudyState;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyTaskNavigationAction;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import com.jetbrains.tmp.learning.statistics.EduUsagesCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public abstract class StepikTaskNavigationAction extends StudyTaskNavigationAction {
    private static final Logger logger = Logger.getInstance(StepikTaskNavigationAction.class);

    public StepikTaskNavigationAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Nullable
    protected abstract Task getDefaultTask(@NotNull final Project project);

    @Override
    public void navigateTask(@NotNull final Project project) {
        StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
        StudyState studyState = new StudyState(studyEditor);
        Task targetTask;
        if (studyState.isValid()) {
            targetTask = getTargetTask(studyState.getTask());
        } else {
            targetTask = getDefaultTask(project);
        }

        if (targetTask == null) {
            return;
        }
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            FileEditorManager.getInstance(project).closeFile(file);
        }
        Map<String, TaskFile> nextTaskFiles = targetTask.getTaskFiles();
        VirtualFile projectDir = project.getBaseDir();
        if (projectDir == null) {
            return;
        }

        VirtualFile taskDir = projectDir.findFileByRelativePath(targetTask.getPath());
        if (taskDir == null) {
            return;
        }
        if (nextTaskFiles.isEmpty()) {
            ProjectView.getInstance(project).select(taskDir, taskDir, false);
            return;
        }

        EduUsagesCollector.taskNavigation();
        VirtualFile shouldBeActive = getFileToActivate(project, nextTaskFiles, taskDir);

        updateProjectView(project, shouldBeActive);

        ToolWindow runToolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.RUN);
        if (runToolWindow != null) {
            runToolWindow.hide(null);
        }
    }

    @Nullable
    protected VirtualFile getFileToActivate(
            @NotNull Project project,
            @NotNull Map<String, TaskFile> nextTaskFiles,
            @NotNull VirtualFile taskDir) {
        VirtualFile shouldBeActive = null;
        VirtualFile srcDir = taskDir.findChild(EduNames.SRC);

        for (Map.Entry<String, TaskFile> entry : nextTaskFiles.entrySet()) {
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
        return shouldBeActive != null ? shouldBeActive : getFirstTaskFile(taskDir, project);
    }
}
