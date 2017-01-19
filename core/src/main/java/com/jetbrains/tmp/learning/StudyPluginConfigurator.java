package com.jetbrains.tmp.learning;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;

public interface StudyPluginConfigurator {
    ExtensionPointName<StudyPluginConfigurator> EP_NAME = ExtensionPointName.create("SCore.studyPluginConfigurator");

    /**
     * Provide action group that should be placed on the tool window toolbar.
     */
    @NotNull
    DefaultActionGroup getActionGroup(Project project);

    /**
     * Provide panels, that could be added to Step tool window.
     *
     * @return Map from panel id, i.e. "Step description", to panel itself.
     */
    @NotNull
    Map<String, JPanel> getAdditionalPanels(Project project);

    @NotNull
    FileEditorManagerListener getFileEditorManagerListener(
            @NotNull final Project project,
            @NotNull final StudyToolWindow toolWindow);

    /**
     * @return parameter for CodeMirror script. Available languages: @see <@linktourl http://codemirror.net/mode/>
     */
    @NotNull
    String getDefaultHighlightingMode();

    @NotNull
    String getLanguageScriptUrl();

    boolean accept(@NotNull final Project project);
}
