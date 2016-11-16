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

    @Override
    public void navigateTask(@NotNull final Project project) {
        StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
        StudyState studyState = new StudyState(studyEditor);
        if (!studyState.isValid()) {
            return;
        }
        Task targetTask = getTargetTask(studyState.getTask());
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
        VirtualFile[] sectionDirs = projectDir.getChildren();
        String lessonDirName = targetTask.getLesson().getDirectory();

        VirtualFile lessonDir = null;
        for (VirtualFile sectionDir : sectionDirs) {
            lessonDir = sectionDir.findChild(lessonDirName);
            if (lessonDir != null) break;
        }

        if (lessonDir == null) {
            return;
        }
        String taskDirName = targetTask.getDirectory();
        VirtualFile taskDir = lessonDir.findChild(taskDirName);
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
            Map<String, TaskFile> nextTaskFiles,
            VirtualFile taskDir) {
        VirtualFile shouldBeActive = null;
        for (Map.Entry<String, TaskFile> entry : nextTaskFiles.entrySet()) {
            String name = entry.getKey();
            VirtualFile srcDir = taskDir.findChild(EduNames.SRC);
            VirtualFile vf = srcDir == null ? taskDir.findChild(name) : srcDir.findChild(name);
            if (vf != null) {
                if (shouldBeActive != null) {
                    FileEditorManager.getInstance(project).openFile(vf, true);
                }
                if (shouldBeActive == null) {
                    shouldBeActive = vf;
                }
            }
        }
        return shouldBeActive != null ? shouldBeActive : getFirstTaskFile(taskDir, project);
    }
}
