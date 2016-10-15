package org.stepik.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.LangSetting;
import com.jetbrains.tmp.learning.StudyState;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.collective.SupportedLanguages;

import javax.swing.*;

import static org.stepik.plugin.actions.DirectivesUtils.*;
import static org.stepik.plugin.collective.SupportedLanguages.JAVA;


public class InsertStepikDirectives extends StudyActionWithShortcut {
    private static final String SHORTCUT = "ctrl alt pressed R";
    private static final String ACTION_ID = "STEPIK.InsertStepikDirectives";


    public InsertStepikDirectives() {

        super("Repair standard template(" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Insert Stepik directives. Repair ordinary template if it is possible.",
                AllIcons.General.ImportSettings);
    }

    @NotNull
    @Override
    public String getActionId() {
        return ACTION_ID;
    }


    @Override
    public String[] getShortcuts() {
        return new String[]{SHORTCUT};
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        StudyEditor studyEditor = StudyUtils.getSelectedStudyEditor(project);
        StudyState studyState = new StudyState(studyEditor);
        if (!studyState.isValid()) {
            return;
        }
        Task targetTask = studyState.getTask();
        if (targetTask == null) {
            return;
        }

        StudyTaskManager taskManager = StudyTaskManager.getInstance(project);
        LangSetting langSetting = taskManager.getLangManager().getLangSetting(targetTask);

        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        for (VirtualFile file : FileEditorManager.getInstance(project).getOpenFiles()) {
            documentManager.saveDocument(documentManager.getDocument(file));
        }

        SupportedLanguages currentLang = SupportedLanguages.loadLangSettings(langSetting.getCurrentLang());

        VirtualFile src = studyState.getTaskDir();
        VirtualFile file = src.findChild(currentLang.getMainFileName());

        String[] text = DirectivesUtils.getFileText(file);

        Pair<Integer, Integer> locations = DirectivesUtils.findDirectives(text, currentLang);
        boolean showHint = StudyTaskManager.getInstance(project).getShowHint();
        if (locations.first == -1 && locations.second == text.length) {
            text = insertDirectives(text, currentLang, showHint);
            if (currentLang.getName().equals(JAVA.getName())) {
                text = insertMainClass(text);
            }
        } else {
            text = removeDirectives(text, locations, showHint, project);
        }
        writeInToFile(text, file, project);
    }
}
