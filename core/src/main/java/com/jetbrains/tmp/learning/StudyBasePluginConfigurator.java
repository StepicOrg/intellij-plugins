package com.jetbrains.tmp.learning;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
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
    public FileEditorManagerListener getFileEditorManagerListener(
            @NotNull Project project,
            @NotNull StudyToolWindow toolWindow) {

        return new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                VirtualFile file = event.getNewFile();
                if (file != null) {
                    StudyNode stepNode = StudyUtils.getStudyNode(event.getManager().getProject(), file);
                    toolWindow.setStepNode(stepNode);
                } else {
                    toolWindow.setStepNode(null);
                }
            }
        };
    }
}
