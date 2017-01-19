package com.jetbrains.tmp.learning;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                StepNode stepNode = StudyUtils.getStep(source.getProject(), file);
                setStepText(stepNode);
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                for (VirtualFile openedFile : source.getOpenFiles()) {
                    if (StudyUtils.getStep(project, openedFile) != null) {
                        return;
                    }
                }
                toolWindow.setEmptyText();
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                VirtualFile file = event.getNewFile();
                if (file != null) {
                    StepNode stepNode = StudyUtils.getStep(event.getManager().getProject(), file);
                    setStepText(stepNode);
                }
            }

            private void setStepText(@Nullable final StepNode stepNode) {
                String text = StudyUtils.getStepTextFromStep(stepNode);
                if (text == null) {
                    toolWindow.setEmptyText();
                    return;
                }
                toolWindow.setStepText(text);
            }
        };
    }
}
