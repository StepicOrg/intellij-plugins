package com.jetbrains.tmp.learning;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collections;
import java.util.Map;

public abstract class StudyBasePluginConfigurator implements StudyPluginConfigurator {
    @NotNull
    public static DefaultActionGroup getDefaultActionGroup() {
        return new DefaultActionGroup();
    }

    @NotNull
    @Override
    public DefaultActionGroup getActionGroup(Project project) {
        return getDefaultActionGroup();
    }

    @NotNull
    @Override
    public Map<String, JPanel> getAdditionalPanels(Project project) {
        return Collections.emptyMap();
    }

    @NotNull
    @Override
    public FileEditorManagerListener getFileEditorManagerListener(@NotNull Project project) {
        return new FileEditorManagerAdapter() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                VirtualFile file = event.getNewFile();
                if (file == null) {
                    return;
                }

                StudyNode stepNode = StudyUtils.getStudyNode(project, file);

                if (stepNode != null) {
                    StepikProjectManager.setSelected(project, stepNode);
                }
            }
        };
    }
}
