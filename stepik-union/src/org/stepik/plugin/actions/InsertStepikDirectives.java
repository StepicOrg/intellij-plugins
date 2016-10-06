package org.stepik.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
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

import static org.stepik.plugin.collective.SupportedLanguages.JAVA;


public class InsertStepikDirectives extends StudyActionWithShortcut {
    public static final String SHORTCUT = "ctrl alt pressed R";
    public static final String ACTION_ID = "STEPIK.InsertStepikDirectives";

    private final String MESSAGE = "Do you want to remove Stepik directives and external code?\n" +
            "You can undo this action using \"ctrl + Z\".";

    public InsertStepikDirectives() {

        super("Insert Stepik directives (" + KeymapUtil.getShortcutText(new KeyboardShortcut(KeyStroke.getKeyStroke(SHORTCUT), null)) + ")",
                "Insert Stepik directives. Repair ordinary template if it is possible.",
//                AllIcons.Actions.EditSource);
                AllIcons.General.ExternalToolsSmall);
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

        SupportedLanguages currentLang = DirectivesUtils.loadLangSettings(langSetting.getCurrentLang());

        VirtualFile src = studyState.getTaskDir();
        VirtualFile file = src.findChild(currentLang.getMainFileName());

        Pair<Integer, Integer> locations = DirectivesUtils.findDirectives(file, currentLang);
        if (locations.first == null && locations.second == null) {
            insertDirectives(file, project, currentLang);
            if (currentLang.getName().equals(JAVA.getName())) {
                insertMainClass(file, project);
            }
        } else {
            removeDirectives(file, locations, project);
        }
    }

    private void removeDirectives(VirtualFile vf, Pair<Integer, Integer> locations, Project project) {
        Document document = FileDocumentManager.getInstance().getDocument(vf);
        String text[] = document.getText().split("\n");

        int start = locations.first == null ? 0 : locations.first;
        int end = locations.second == null ? text.length : locations.second;

        if (start > 1 || text.length - end > 2) {
            int information = Messages.showYesNoDialog(project, MESSAGE, "Information", Messages.getInformationIcon());
            if (information != 0) return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = start + 1; i < end; i++) {
            sb.append(text[i]);
            sb.append("\n");
        }

        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(
                        () -> document.setText(sb.toString())), "Remove Stepik directives", "Remove Stepik directives");
    }

    private void insertMainClass(VirtualFile vf, Project project) {
        Document document = FileDocumentManager.getInstance().getDocument(vf);
        String text[] = document.getText().split("\n");
        StringBuilder sb = new StringBuilder();
        sb.append("class Main {\n");
        for (String tmp : text) {
            sb.append("\t");
            sb.append(tmp);
            sb.append("\n");
        }
        sb.append("}");

        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(
                        () -> document.setText(sb.toString())), "Insert Main class", "Insert Main class");

    }

    private void insertDirectives(VirtualFile vf, Project project, SupportedLanguages lang) {
        Document document = FileDocumentManager.getInstance().getDocument(vf);
        String text = document.getText();
        StringBuilder sb = new StringBuilder();
        sb.append(lang.getComment()).append("Stepik code: start\n");
        sb.append(text);
        sb.append("\n").append(lang.getComment()).append("Stepik code: end\n");

        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(
                        () -> document.setText(sb.toString())), "Insert Stepik directives", "Insert Stepik directives");

    }
}
